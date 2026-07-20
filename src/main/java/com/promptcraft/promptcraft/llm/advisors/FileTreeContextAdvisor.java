package com.promptcraft.promptcraft.llm.advisors;

import com.promptcraft.promptcraft.dto.file.FileNode;
import com.promptcraft.promptcraft.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileTreeContextAdvisor implements StreamAdvisor {

    private final FileService fileService;

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        Map<String, Object> context = chatClientRequest.context();
        Long projectId = Long.parseLong(context.getOrDefault("projectId", 0).toString());

        ChatClientRequest augmentedChatClientRequest = augmentedRequestWithFiletree(chatClientRequest, projectId);

        return streamAdvisorChain.nextStream(augmentedChatClientRequest);
    }

    private ChatClientRequest augmentedRequestWithFiletree(ChatClientRequest request, Long projectId){

        List<FileNode> fileTree =  fileService.getFileTree(projectId);
        String fileTreeContext = "\n\n ----- FILE TREE ------\n\n" + fileTree.toString();

        return null;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
