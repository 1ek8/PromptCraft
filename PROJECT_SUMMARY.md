# PromptCraft — Project Summary

## Overview

**PromptCraft** is an AI-powered app builder (inspired by Lovable.dev) that lets users describe software projects in natural language and have an AI generate the code in real time. Built with Spring Boot 4, Java 21, and PostgreSQL.

---

## Tech Stack

| Category            | Technology                                      |
|---------------------|-------------------------------------------------|
| Framework           | Spring Boot 4.0.1, Spring MVC                   |
| Language            | Java 21                                         |
| Database            | PostgreSQL (via Spring Data JPA / Hibernate)    |
| Security            | Spring Security + JWT (jjwt 0.12.6)             |
| AI / LLM           | Spring AI (OpenAI / OpenRouter)                 |
| Payments            | Stripe (stripe-java 32.1.0)                     |
| File Storage        | MinIO (S3-compatible object storage)            |
| Object Mapping      | MapStruct 1.6.3                                 |
| Boilerplate        | Lombok                                          |
| API Documentation   | Springdoc OpenAPI (Swagger UI)                  |
| Build              | Maven                                           |

---

## Architecture

```mermaid
graph TD
    Client[Client / Postman]
    SW[Swagger UI]

    subgraph "Spring Boot Application"
        subgraph "Controllers Layer"
            AC[AuthController]
            PC[ProjectController]
            CC[ChatController]
            FC[FileController]
            PAC[ParticipantController]
            BC[BillingController]
            UC[UsageController]
        end

        subgraph "Security Layer"
            JWT[JwtAuthFilter]
            SEC[SecurityExpressions]
            AU[AuthUtil]
        end

        subgraph "Service Layer"
            AS[AuthService]
            PS[ProjectService]
            AGS[AIGenerationService]
            FS[FileService]
            PAS[ParticipantService]
            PLS[PlanService]
            SS[SubscriptionService]
            US[UsageService]
            PPS[PaymentProcessor]
        end

        subgraph "Repository Layer"
            UR[UserRepository]
            PR[ProjectRepository]
            PAR[ParticipantRepository]
            FR[FileRepository]
            PLR[PlanRepository]
            SR[SubscriptionRepository]
        end

        subgraph "External Integrations"
            AI[OpenAI / OpenRouter]
            STRIPE[Stripe API]
            MINIO[MinIO Storage]
        end
    end

    DB[(PostgreSQL)]

    Client --> AC & PC & CC & FC & PAC & BC & UC
    Client --> SW

    AC & PC & CC & FC & PAC & BC --> JWT
    JWT --> SEC

    AC --> AS
    PC --> PS
    CC --> AGS
    FC --> FS
    PAC --> PAS
    BC --> PLS & SS & PPS
    UC --> US

    AS & PS & PAS --> UR & PR & PAR
    PLS & SS --> PLR & SR
    FS --> FR

    UR & PR & PAR & FR & PLR & SR --> DB
    AGS --> AI
    PPS --> STRIPE
    FS --> MINIO
```

---

## Endpoints

### Auth — `/api/auth`

| Method | Path              | Request Body                                     | Response                  | Auth Required |
|--------|-------------------|--------------------------------------------------|---------------------------|---------------|
| POST   | `/api/auth/signup`| `{ "username", "name", "password" }`             | `AuthResponse` (token + user) | ❌         |
| POST   | `/api/auth/login` | `{ "username", "password" }`                     | `AuthResponse` (token + user) | ❌         |
| GET    | `/api/auth/me`    | —                                                | `UserProfileResponse`     | ❌ (hardcoded 1L) |

### Projects — `/api/projects`

| Method | Path                 | Request Body   | Response                        | Auth Required                          |
|--------|----------------------|----------------|---------------------------------|----------------------------------------|
| GET    | `/api/projects`      | —              | `List<ProjectSummaryResponse>`  | ❌ (commented out)                     |
| GET    | `/api/projects/{id}` | —              | `ProjectResponse`               | ✅ `@PreAuthorize("@security.canViewProject")` |
| POST   | `/api/projects`      | `{ "name" }`   | `ProjectResponse` (201)         | ❌ (no check)                          |
| PATCH  | `/api/projects/{id}` | `{ "name" }`   | `ProjectResponse`               | ✅ `@PreAuthorize("@security.canEditProject")` |
| DELETE | `/api/projects/{id}` | —              | 204                             | ✅ `@PreAuthorize("@security.canDeleteProject")` |

### Chat — `/api/chat/stream`

| Method | Path               | Request Body                         | Response                            | Auth Required |
|--------|--------------------|--------------------------------------|-------------------------------------|---------------|
| POST   | `/api/chat/stream` | `{ "message", "projectId" }`        | `Flux<ServerSentEvent<String>>` (SSE) | ✅ (in service layer) |

### Files — `/api/projects/{projectId}/files`

| Method | Path                                      | Response                    | Auth Required |
|--------|-------------------------------------------|-----------------------------|---------------|
| GET    | `/api/projects/{projectId}/files`         | `List<FileNode>`            | ❌ (hardcoded 1L) |
| GET    | `/api/projects/{projectId}/files/{*path}` | `FileContentResponse`       | ❌ (hardcoded 1L) |

### Participants — `/api/projects/{projectId}/members`

| Method | Path                                                        | Request Body                     | Response                  | Auth Required                                           |
|--------|-------------------------------------------------------------|----------------------------------|---------------------------|---------------------------------------------------------|
| GET    | `/api/projects/{projectId}/members`                         | —                                | `List<ParticipantResponse>` | ✅ `@PreAuthorize("@security.canViewMembers")`        |
| POST   | `/api/projects/{projectId}/members`                         | `{ "username", "role" }`        | `ParticipantResponse` (201) | ✅ `@PreAuthorize("@security.canManageMembers")`      |
| PATCH  | `/api/projects/{projectId}/members/{participantId}`         | `{ "role" }`                    | `ParticipantResponse`      | ✅ `@PreAuthorize("@security.canManageMembers")`      |
| DELETE | `/api/projects/{projectId}/members/{participantId}`         | —                                | 204                        | ✅ `@PreAuthorize("@security.canManageMembers")`      |

### Billing / Subscriptions

| Method | Path                          | Request Body                    | Response                     | Auth Required              |
|--------|-------------------------------|---------------------------------|------------------------------|----------------------------|
| GET    | `/api/plans`                  | —                               | `PlanResponse`               | ❌ Public                  |
| GET    | `/api/me/subscription`        | —                               | `SubscriptionResponse`       | ❌ (no auth extraction)    |
| POST   | `/api/payments/checkout`      | `{ "planId" }`                  | `CheckoutResponse` (Stripe URL) | ❌ (no auth extraction) |
| POST   | `/api/payments/portal`        | —                               | `PortalResponse` (Stripe URL)   | ❌ (no auth extraction) |
| POST   | `/webhooks/payment`           | Raw JSON + `Stripe-Signature` header | 200                       | ❌ Public (webhook)        |

### Usage — `/api/usage`

| Method | Path                    | Response                       | Auth Required              |
|--------|-------------------------|--------------------------------|----------------------------|
| GET    | `/api/usage/today`      | `UsageTodayResponse`           | ❌ (hardcoded 1L)          |
| GET    | `/api/usage/limits`     | `PlanLimitsResponse`           | ❌ (hardcoded 1L)          |

---

## Database Schema

### Entity Relationship Diagram

```mermaid
erDiagram
    user_table ||--o{ project_participant_table : "participates"
    user_table ||--o{ subscription : "has"
    user_table ||--o{ usage_log : "logs"
    user_table ||--o{ chat_sessions : "chats"

    project_table ||--o{ project_participant_table : "has members"
    project_table ||--o{ project_files : "contains"
    project_table ||--o{ preview : "previews"
    project_table ||--o{ usage_log : "logs"
    project_table ||--o{ chat_sessions : "has sessions"

    chat_sessions ||--o{ chat_messages : "contains"

    plan ||--o{ subscription : "defines"

    subscription ||--o| user_table : "belongs to"
    subscription ||--o| plan : "is for"

    user_table {
        bigint Id PK
        varchar username UK
        varchar name
        varchar password
        varchar stripe_customer_id UK
        instant created_at
        instant updated_at
        instant deleted_at
    }

    project_table {
        bigint Id PK
        varchar name
        boolean is_public
        instant created_at
        instant updated_at
        instant deleted_at
    }

    project_participant_table {
        bigint project_id PK,FK
        bigint user_id PK,FK
        varchar project_role
        instant invited_at
        instant accepted_at
    }

    project_files {
        bigint Id PK
        bigint project_id FK
        varchar path
        varchar minio_object_key
        instant created_at
        instant updated_at
    }

    plan {
        bigint Id PK
        varchar name
        varchar stripe_price_id UK
        integer max_projects
        integer max_tokens_per_day
        integer max_previews
        boolean active
    }

    subscription {
        bigint Id PK
        bigint user_id FK
        bigint plan_id FK
        varchar status
        varchar stripe_customer_id
        varchar stripe_subscription_id
        instant current_period_start
        instant current_period_end
        boolean canceled_at_period_end
        instant created_at
        instant updated_at
    }

    chat_sessions {
        bigint project_id PK,FK
        bigint user_id PK,FK
        instant created_at
        instant updated_at
        instant deleted_at
    }

    chat_messages {
        bigint Id PK
        bigint project_id FK
        bigint user_id FK
        text content
        varchar role
        integer tokens_used
        instant created_at
    }

    usage_log {
        bigint Id PK
        bigint user_id FK
        bigint project_id FK
        varchar action
        integer tokens_used
        integer duration_ms
        varchar meta_data
        instant created_at
    }

    preview {
        bigint Id PK
        bigint project_id FK
        varchar namespace
        varchar pod_name
        varchar preview_url
        instant created_at
        instant terminated_at
    }
```

### Table Details

| Table                          | Key Columns & Notes                                                                 |
|--------------------------------|--------------------------------------------------------------------------------------|
| `user_table`                   | `id` PK, `username` (unique), `password` (bcrypt), `stripe_customer_id` (unique)    |
| `project_table`                | `id` PK, `name`, `is_public`, `deleted_at` for soft deletes. Indexes on `(updated_at DESC, deleted_at)` and `(deleted_at)` |
| `project_participant_table`    | Composite PK `(project_id, user_id)`, `project_role` enum (OWNER, EDITOR, VIEWER)   |
| `project_files`                | `id` PK, `project_id` FK → project, `path`, `minio_object_key` for object storage   |
| `plan`                         | `id` PK, `stripe_price_id` (unique), `max_projects`, `max_tokens_per_day`, `max_previews` |
| `subscription`                 | `id` PK, `user_id` FK, `plan_id` FK, `status` enum (ACTIVE, TRAILING, CANCELLED, PAST_DUE, INCOMPLETE) |
| `chat_sessions`                | Composite PK `(project_id, user_id)` — one session per user per project              |
| `chat_messages`                | `id` PK, FK to `chat_sessions` via `(project_id, user_id)`, `content` (text), `role` (USER, ASSISTANT, SYSTEM, TOOL) |
| `usage_log`                    | `id` PK, `user_id` FK, `project_id` FK, `action`, `tokens_used`, `duration_ms`      |
| `preview`                      | `id` PK, `project_id` FK, `namespace`/`pod_name` (Kubernetes), `preview_url`        |

---

## Enums

| Enum                | Values                                                      | Permission Mapping (ProjectRole → ProjectPermission) |
|---------------------|-------------------------------------------------------------|------------------------------------------------------|
| `ProjectRole`       | `OWNER`, `EDITOR`, `VIEWER`                                 | **OWNER**: VIEW, EDIT, DELETE, MANAGE_MEMBERS, VIEW_MEMBERS |
| `ProjectPermission` | `VIEW`, `EDIT`, `DELETE`, `MANAGE_MEMBERS`, `VIEW_MEMBERS`  | **EDITOR**: VIEW, EDIT                               |
| `SubscriptionStatus`| `ACTIVE`, `TRAILING`, `CANCELLED`, `PAST_DUE`, `INCOMPLETE`| **VIEWER**: VIEW                                     |
| `MessageRole`       | `USER`, `ASSISTANT`, `SYSTEM`, `TOOL`                       |                                                      |
| `PreviewStatus`     | `TERMINATED`, `CREATING`, `FAILED`, `RUNNING`               |                                                      |

---

## Key Workflows

### 1. Authentication Flow

```mermaid
sequenceDiagram
    participant C as Client
    participant AC as AuthController
    participant AS as AuthService
    participant UR as UserRepository
    participant JWT as AuthUtil (JWT)

    C->>AC: POST /api/auth/signup { username, name, password }
    AC->>AS: signup(request)
    AS->>UR: check username uniqueness
    AS->>AS: encode password (BCrypt)
    AS->>UR: save User entity
    AS->>JWT: generateAccessToken(user)
    JWT-->>AS: JWT string (sub=username, userId, 10min expiry)
    AS-->>AC: AuthResponse { token, user }
    AC-->>C: 200 { "token": "eyJhbGci...", "user": {...} }
```

### 2. Project Creation Flow

```mermaid
sequenceDiagram
    participant C as Client
    participant PC as ProjectController
    participant PS as ProjectService
    participant PR as ProjectRepository
    participant SR as SubscriptionRepository
    participant PAR as ParticipantRepository

    C->>PC: POST /api/projects { "name": "My App" }
    PC->>PS: createProject(request)
    PS->>SR: canCreateNewProject() — checks plan limits
    alt Over Project Limit
        PS-->>PC: throw exception
        PC-->>C: 400 / 403
    else Within Limit
        PS->>PR: save Project
        PS->>PAR: add OWNER participant (current user)
        PS-->>PC: ProjectResponse
        PC-->>C: 201 ProjectResponse
    end
```

### 3. AI Chat Streaming Flow

```mermaid
sequenceDiagram
    participant C as Client
    participant CC as ChatController
    participant AI as AIGenerationService
    participant FS as FileService
    participant MINIO as MinIO Storage
    participant FR as FileRepository
    participant LLM as OpenAI / OpenRouter

    C->>CC: POST /api/chat/stream { message, projectId }
    CC->>AI: streamResponse(message, projectId)
    AI->>LLM: ChatClient.stream() — sends system prompt + user message
    Note over AI,LLM: System prompt: "You are a React architect..."
    loop SSE Stream
        LLM-->>AI: streaming tokens
        AI->>AI: parse for <file path="...">...</file> tags
        alt File Tag Found
            AI->>FS: saveFile(projectId, path, content)
            FS->>MINIO: putObject (projectId/path)
            FS->>FR: upsert ProjectFile record
        end
        AI-->>CC: Flux<String> (processed data)
        CC-->>C: ServerSentEvent<String> (SSE stream)
    end
```

### 4. Collaboration (Invite Member)

```mermaid
sequenceDiagram
    participant C as Client
    participant PAC as ParticipantController
    participant PAS as ParticipantService
    participant PAR as ParticipantRepository
    participant UR as UserRepository

    C->>PAC: POST /api/projects/{projectId}/members { username, role }
    Note over C,PAC: Requires @PreAuthorize("@security.canManageMembers")
    PAC->>PAS: inviteParticipant(projectId, request)
    PAS->>UR: find user by username
    PAS->>PAR: create ProjectParticipant record
    PAS-->>PAC: ParticipantResponse
    PAC-->>C: 201 ParticipantResponse
```

### 5. Subscription & Payment Flow

```mermaid
sequenceDiagram
    participant C as Client
    participant BC as BillingController
    participant SP as StripePaymentProcessor
    participant STRIPE as Stripe API
    participant SS as SubscriptionService
    participant DB as Database

    C->>BC: POST /api/payments/checkout { planId }
    BC->>SP: createCheckoutSessionUrl(request)
    SP->>STRIPE: Session.create() with metadata {user_id, plan_id}
    STRIPE-->>SP: Checkout Session URL
    SP-->>BC: CheckoutResponse { checkoutUrl }
    BC-->>C: 200 { checkoutUrl }

    C->>STRIPE: User completes payment in Stripe Checkout
    STRIPE-->>BC: POST /webhooks/payment (checkout.session.completed)
    BC->>SP: handleWebhookEvent()
    SP->>SS: activateSubscription(userId, planId, subId, customerId)
    SS->>DB: save Subscription record
    BC-->>STRIPE: 200 OK
```

---

## Current Implementation Status

| Component       | Status         | Notes                                                          |
|-----------------|----------------|----------------------------------------------------------------|
| Auth            | ✅ Implemented | Signup, login, JWT generation. `getProfile()` returns null.   |
| Projects        | ✅ Implemented | CRUD with soft delete. Plan limit check working.               |
| Chat (AI)       | ✅ Implemented | SSE streaming, file tag parsing, MinIO save. Needs API key.   |
| Files           | ⚠️ Partially   | `saveFile()` works. `getFileTree()` and `getFileContent()` are stubs returning null/empty. |
| Participants    | ✅ Implemented | Full CRUD with role-based permissions enforced.               |
| Plans           | ⚠️ Stub        | `getAllActivePlans()` returns null. Needs seed data or real query. |
| Subscriptions   | ✅ Implemented | CRUD methods, status transitions, project limit enforcement.  |
| Usage           | ⚠️ Stub        | `getTodayUsage()` and `getCurrentLimit()` return null.        |
| Payments        | ✅ Implemented | Stripe checkout, portal, webhook handling (5 event types).   |
| Previews        | ❌ Not started  | Entity exists, no service logic yet.                          |

### Known Issues & TODOs

- **AI API key misconfigured**: `application.yaml` has `ai.openai.api-key` set to a URL (`https://openrouter.ai/api`) instead of an actual API key. Set via environment variable.
- **Hardcoded userId=1L**: Several controllers use placeholder user IDs instead of extracting from JWT security context.
- **No seed data**: The `plan` table starts empty. Plans must be inserted manually into PostgreSQL for billing to work.
- **`ddl-auto: create`**: Schema is dropped and recreated on every restart — development only.
- **File service stubs**: `getFileTree` and `getFileContent` return null/empty.
- **Usage service stubs**: Usage tracking and plan limit queries return null.
- **JWT expiry**: Token expires in 10 minutes (`600` seconds in `AuthUtil`).

---

## Configuration Reference

```yaml
# src/main/resources/application.yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:postgresql://localhost:5434/promptcraft
    username: promptcraft
    password: promptcraft123
  jpa:
    hibernate:
      ddl-auto: create    # Development only
    show-sql: true

jwt:
  secret-key: my-jwt-secret-key-which-is-hopefully-256-bits-long

stripe:
  api:
    secret: ${STRIPE_TEST_SECRET_KEY:sk_test_placeholder}
  webhook:
    secret: whsec_659523b32d50f5a69021628904449886b8188e01e2da64ac2ce0f2cd863a0aed

ai:
  openai:
    api-key: <REQUIRED: set via env var or update this field>
    chat:
      options:
        model: gpt-4    # or whatever model you want
        temperature: 0.0

client:
  url: http://localhost:8080/

minio:
  project-bucket: promptcraft-projects  # Used by FileServiceImpl
```

---

## Building & Running

```bash
# Prerequisites: Java 21, Maven, PostgreSQL (port 5434), MinIO

# Build
./mvnw clean compile

# Run
./mvnw spring-boot:run

# Tests
./mvnw test

# OpenAPI docs (when running)
# http://localhost:8080/swagger-ui.html
# http://localhost:8080/v3/api-docs
```
