# Task Creation Sequence Diagram

This diagram illustrates the sequence flow when creating a new task in the Task Manager application.

```mermaid
sequenceDiagram
    participant C as Client
    participant TC as TaskController
    participant TS as TaskService
    participant SC as SecurityContext
    participant UR as UserRepository
    participant TV as TaskValidator
    participant TR as TaskRepository
    participant MTR as MongoTaskRepository
    participant DB as MongoDB

    C->>+TC: POST /api/v1/tasks (CreateTaskRequest)
    Note over TC: Generate requestId

    TC->>+TS: createTask(request, requestId)
    TS->>TS: validateCreateTaskRequest(request, requestId)
    
    TS->>+SC: getCurrentContext()
    SC-->>-TS: Authentication
    
    TS->>+UR: findByUsername(username)
    UR-->>-TS: User
    
    Note over TS: Create Task domain object with UUID and NOT_STARTED status
    
    TS->>+TR: save(task)
    TR->>+MTR: save(task)
    MTR->>MTR: Convert to TaskDocument
    MTR->>+DB: save(taskDocument)
    DB-->>-MTR: savedDocument
    MTR->>MTR: Convert to domain Task
    MTR-->>-TR: Task
    TR-->>-TS: Task
    
    TS-->>-TC: Task
    TC-->>-C: HTTP 201 Created with Task

```

## Task Creation Flow Explanation

1. **Client Request**:
   - The client sends a POST request to `/api/v1/tasks` with a `CreateTaskRequest` body
   - The request contains task name, description, due date, priority, tags, and user

2. **Controller Processing**:
   - `TaskController` receives the request and generates a unique request ID for tracking
   - The controller delegates to the service layer for business logic

3. **Service Layer**:
   - `TaskService` validates the incoming request (name, priority, tags, etc.)
   - Retrieves the current user ID from the security context for ownership assignment
   - Creates a new Task domain object with a generated UUID and NOT_STARTED status

4. **Persistence Layer**:
   - The task is passed to the repository layer for persistence
   - `MongoTaskRepository` converts the domain Task to a TaskDocument
   - The document is saved to MongoDB
   - The saved document is converted back to a domain Task object

5. **Response**:
   - The saved Task is returned to the controller
   - The controller returns an HTTP 201 Created response with the Task in the body

This sequence demonstrates the clean separation between controller, service, and repository layers, adhering to good architectural practices. The use of domain objects and DTOs for data transfer between layers provides clear boundaries and promotes a well-structured system.