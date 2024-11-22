package com.grabduck.taskmanager.service;

import com.grabduck.taskmanager.domain.Task;
import com.grabduck.taskmanager.domain.TaskPriority;
import com.grabduck.taskmanager.domain.TaskStatus;
import com.grabduck.taskmanager.domain.Page;
import com.grabduck.taskmanager.domain.SortOption;
import com.grabduck.taskmanager.dto.CreateTaskRequest;
import com.grabduck.taskmanager.exception.InvalidTaskException;
import com.grabduck.taskmanager.exception.TaskNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class TaskService {

    private static final Task DUMMY_TASK = Task.createNew(
            "Sample Task",
            "This is a sample task description",
            LocalDateTime.now().plusDays(7),
            TaskPriority.HIGH,
            Set.of("sample", "dummy")
    );

    public Task createTask(CreateTaskRequest request) {
        validateCreateTaskRequest(request);
        return Task.createNew(
                request.name(),
                request.description(),
                request.dueDate(),
                request.priority(),
                request.tags()
        );
    }

    public Task getTaskById(UUID taskId) {
        if (taskId == null) {
            throw new InvalidTaskException("Task ID cannot be null");
        }
        // In a real implementation, we would check if the task exists in the database
        // For now, we'll simulate a not found scenario for a specific ID
        if (taskId.equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
            throw new TaskNotFoundException(taskId);
        }
        return new Task(
                taskId,
                DUMMY_TASK.name(),
                DUMMY_TASK.description(),
                DUMMY_TASK.dueDate(),
                DUMMY_TASK.status(),
                DUMMY_TASK.priority(),
                DUMMY_TASK.tags()
        );
    }

    public Task updateTask(UUID taskId, Task task) {
        if (taskId == null) {
            throw new InvalidTaskException("Task ID cannot be null");
        }
        validateTask(task);
        // In a real implementation, we would check if the task exists in the database
        // For now, we'll simulate a not found scenario for a specific ID
        if (taskId.equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
            throw new TaskNotFoundException(taskId);
        }
        return new Task(
                taskId,
                task.name(),
                task.description(),
                task.dueDate(),
                task.status(),
                task.priority(),
                task.tags()
        );
    }

    public void deleteTask(UUID taskId) {
        if (taskId == null) {
            throw new InvalidTaskException("Task ID cannot be null");
        }
        // In a real implementation, we would check if the task exists in the database
        // For now, we'll simulate a not found scenario for a specific ID
        if (taskId.equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
            throw new TaskNotFoundException(taskId);
        }
        // Dummy implementation - no action needed
    }

    public Page<Task> getTasks(
            String search,
            TaskStatus status,
            TaskPriority priority,
            String tag,
            int page,
            int size,
            SortOption sortOption
    ) {
        // Create a list of dummy tasks
        List<Task> dummyTasks = List.of(
                DUMMY_TASK,
                Task.createNew(
                        "Another Task",
                        "Another task description",
                        LocalDateTime.now().plusDays(3),
                        TaskPriority.MEDIUM,
                        Set.of("work", "important")
                ),
                Task.createNew(
                        "Urgent Task",
                        "This needs immediate attention",
                        LocalDateTime.now().plusDays(1),
                        TaskPriority.URGENT,
                        Set.of("urgent", "critical")
                )
        );

        // In a real implementation, we would:
        // 1. Convert the sort string to proper sort criteria
        // 2. Apply filters based on search, status, priority, and tag
        // 3. Apply pagination
        // 4. Return actual data from the database

        // For now, return dummy data
        return new Page<>(
                dummyTasks,
                dummyTasks.size(),
                1,
                size,
                page
        );
    }

    private void validateTask(Task task) {
        if (task == null) {
            throw new InvalidTaskException("Task cannot be null");
        }
        if (task.name() == null || task.name().trim().isEmpty()) {
            throw new InvalidTaskException("Task name cannot be empty");
        }
        if (task.name().length() > 255) {
            throw new InvalidTaskException("Task name cannot be longer than 255 characters");
        }
        if (task.description() != null && task.description().length() > 1000) {
            throw new InvalidTaskException("Task description cannot be longer than 1000 characters");
        }
        if (task.tags() == null || task.tags().isEmpty()) {
            throw new InvalidTaskException("Task must have at least one tag");
        }
        if (task.tags().size() > 10) {
            throw new InvalidTaskException("Task cannot have more than 10 tags");
        }
        if (task.dueDate() != null && task.dueDate().isBefore(LocalDateTime.now())) {
            throw new InvalidTaskException("Task due date cannot be in the past");
        }
    }

    private void validateCreateTaskRequest(CreateTaskRequest request) {
        if (request == null) {
            throw new InvalidTaskException("Task request cannot be null");
        }
        if (request.name() == null || request.name().trim().isEmpty()) {
            throw new InvalidTaskException("Task name cannot be empty");
        }
        if (request.name().length() > 255) {
            throw new InvalidTaskException("Task name cannot be longer than 255 characters");
        }
        if (request.description() != null && request.description().length() > 1000) {
            throw new InvalidTaskException("Task description cannot be longer than 1000 characters");
        }
        if (request.tags() == null || request.tags().isEmpty()) {
            throw new InvalidTaskException("Task must have at least one tag");
        }
    }
}
