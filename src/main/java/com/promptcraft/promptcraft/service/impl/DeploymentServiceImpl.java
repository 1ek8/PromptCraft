package com.promptcraft.promptcraft.service.impl;

import com.promptcraft.promptcraft.dto.deploy.DeployResponse;
import com.promptcraft.promptcraft.service.DeploymentService;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.ExecListener;
import io.fabric8.kubernetes.client.dsl.ExecWatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeploymentServiceImpl implements DeploymentService {

    private final KubernetesClient kubernetesClient;

    private static final String NAMESPACE = "promptcraft-apps";
    private static final String POOL_LABEL = "status";
    private static final String PROJECT_LABEL = "project-id";
    private static final String IDLE = "idle";
    private static final String BUSY = "busy";
    private static final String SYNCER_CONTAINER = "syncer";
    private static final String RUNNER_CONTAINER = "runner";
    private static final String REVERSE_PROXY_PORT = "8090";
    private static final String MINIO_ALIAS = "myminio";
    private static final String PROJECTS_BUCKET = "projects";

    private static final long SYNC_TIMEOUT_SECONDS = 120;
    private static final long INSTALL_TIMEOUT_SECONDS = 300;
    private static final long SHORT_TIMEOUT_SECONDS = 30;
    private static final long DEV_SERVER_READY_TIMEOUT_SECONDS = 90;

    @Override
    public DeployResponse deploy(Long projectId) {

        String domain = "project-" + projectId + ".app.byaniket.site";

        Pod existingPod = findActivePod(projectId);

        if(existingPod != null) {
            return new DeployResponse("http://" + domain + ":" + REVERSE_PROXY_PORT);
        }

        return claimAndStartNewPod(projectId, domain);
    }

    private DeployResponse claimAndStartNewPod(Long projectId, String domain) {

        Pod pod = kubernetesClient.pods().inNamespace(NAMESPACE)
                .withLabel(POOL_LABEL, IDLE)
                .list().getItems().stream()
                .findFirst()
                .orElseThrow( () -> new RuntimeException("No idle runners available. Please scale up the runner pool."));

        String podName = pod.getMetadata().getName();
        log.info("Claiming pod {} for project {}", podName, projectId);

        kubernetesClient.pods().inNamespace(NAMESPACE).withName(podName).edit(p -> {
            p.getMetadata().getLabels().put(POOL_LABEL, BUSY);
            p.getMetadata().getLabels().put(PROJECT_LABEL, projectId.toString());
            return p;
        });

        // 1. Copy the project's source files from MinIO into the shared /app volume.
        String initialSyncCmd = String.format(
                "rm -rf /app/* && mc mirror --overwrite %s/%s/%d/ /app/",
                MINIO_ALIAS, PROJECTS_BUCKET, projectId);

        log.info("Starting initial sync for project {} in pod {}", projectId, podName);
        execCommand(podName, SYNCER_CONTAINER, SYNC_TIMEOUT_SECONDS, "sh", "-c", initialSyncCmd);

        // 2. Keep the workspace in sync while the dev server runs.
        String watchCmd = String.format(
                "nohup mc mirror --overwrite --watch %s/%s/%d/ /app/ > /app/sync.log 2>&1 &",
                MINIO_ALIAS, PROJECTS_BUCKET, projectId);
        execCommand(podName, SYNCER_CONTAINER, SHORT_TIMEOUT_SECONDS, "sh", "-c", watchCmd);

        // 3. Install dependencies and WAIT for it to finish. Starting vite before
        //    node_modules exists fails immediately, so these two steps cannot be chained
        //    in a single backgrounded command.
        log.info("Installing dependencies for project {} ...", projectId);
        execCommand(podName, RUNNER_CONTAINER, INSTALL_TIMEOUT_SECONDS, "sh", "-c",
                "cd /app && npm install --no-audit --no-fund");

        // 4. Start the dev server detached, so it survives the exec session closing.
        log.info("Starting dev server for project {} ...", projectId);
        execCommand(podName, RUNNER_CONTAINER, SHORT_TIMEOUT_SECONDS, "sh", "-c",
                "cd /app && setsid nohup npm run dev -- --host 0.0.0.0 --port 5173 > /app/dev.log 2>&1 < /dev/null &");

        // 5. Wait until it actually answers before handing the URL back.
        waitForDevServer(podName, projectId);

        log.info("Deployment successful: http://{}:{}", domain, REVERSE_PROXY_PORT);
        return new DeployResponse("http://" + domain + ":" + REVERSE_PROXY_PORT);
    }

    private void waitForDevServer(String podName, Long projectId) {

        String command = "for i in $(seq 1 30); do "
                + "wget -qO- -T1 http://127.0.0.1:5173 >/dev/null 2>&1 && { echo READY; exit 0; }; "
                + "sleep 1; "
                + "done; echo NOT_READY; tail -n 20 /app/dev.log";

        String output = execCommand(podName, RUNNER_CONTAINER, DEV_SERVER_READY_TIMEOUT_SECONDS,
                "sh", "-c", command);

        if (output.contains("READY")) {
            log.info("Dev server for project {} is responding on port 5173", projectId);
        } else {
            log.warn("Dev server for project {} did not respond in time. Last log lines:\n{}",
                    projectId, output);
        }
    }

    /**
     * Runs a command in a container and returns whatever it wrote to stdout/stderr.
     * Commands ending in "&" are treated as background jobs: they never close the exec
     * session on their own, so we give them a moment to start and return.
     */
    private String execCommand(String podName, String container, long timeoutSeconds, String... command) {

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        CompletableFuture<Void> finished = new CompletableFuture<>();

        try (
            ExecWatch ignored = kubernetesClient.pods().inNamespace(NAMESPACE).withName(podName)
                    .inContainer(container)
                    .writingOutput(output)
                    .writingError(output)
                    .usingListener(new ExecListener() {
                        @Override
                        public void onClose(int code, String reason) {
                            finished.complete(null);
                        }
                    })
                    .exec(command)) {

            if (command[command.length - 1].trim().endsWith("&")) {
                Thread.sleep(500);
            } else {
                finished.get(timeoutSeconds, TimeUnit.SECONDS);
            }

            return output.toString(StandardCharsets.UTF_8);
        } catch (TimeoutException e) {
            throw new RuntimeException(
                    "Timed out after " + timeoutSeconds + "s running command in pod " + podName, e);
        } catch (Exception e) {
            log.error("Exec failed in pod {} (container {})", podName, container, e);
            throw new RuntimeException("Pod execution failed", e);
        }
    }

    Pod findActivePod(Long projectId) {
        return kubernetesClient.pods().inNamespace(NAMESPACE)
                .withLabel(PROJECT_LABEL, projectId.toString())
                .withLabel(POOL_LABEL, BUSY)
                .list().getItems().stream()
                .filter(pod -> "Running".equals(pod.getStatus().getPhase()))
                .findFirst()
                .orElse(null);
    }
}
