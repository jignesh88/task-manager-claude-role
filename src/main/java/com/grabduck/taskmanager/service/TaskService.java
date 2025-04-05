package com.grabduck.taskmanager.service;

import com.grabduck.taskmanager.domain.Task;
import com.grabduck.taskmanager.domain.TaskPriority;
import com.grabduck.taskmanager.domain.TaskStatus;
import com.grabduck.taskmanager.domain.Page;
import com.grabduck.taskmanager.domain.SortOption;
import com.grabduck.taskmanager.dto.CreateTaskRequest;
import com.grabduck.taskmanager.exception.InvalidTaskException;
import com.grabduck.taskmanager.exception.TaskNotFoundException;
import com.grabduck.taskmanager.repository.TaskRepository;
import com.grabduck.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private UUID getCurrentUserId() {
        try {
            String username = ((UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal()).getUsername();
            
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalStateException("Current user not found"))
                    .id();
        } catch (Exception e) {
            log.error("Failed to get current user ID", e);
            throw new IllegalStateException("Failed to get current user ID", e);
        }
    }

    public Task createTask(CreateTaskRequest request, String requestId) {
        try {
            validateCreateTaskRequest(request, requestId);
            UUID ownerId = getCurrentUserId();
            
            Task task = new Task(
                    UUID.randomUUID(),
                    request.name(),
                    request.description(),
                    request.dueDate(),
                    TaskStatus.NOT_STARTED,
                    request.priority(),
                    request.tags(),
                    ownerId
            );
            return taskRepository.save(task);
        } catch (Exception e) {
            log.error("Failed to create task with requestId {}: {}", requestId, request, e);
            throw new RuntimeException("Failed to create task due to database error", e);
        }
    }

    public Task getTaskById(UUID taskId) {
        try {
            if (taskId == null) {
                throw new InvalidTaskException("Task ID cannot be null");
            }
            UUID currentUserId = getCurrentUserId();
            return taskRepository.findById(taskId, currentUserId)
                    .orElseThrow(() -> new TaskNotFoundException(taskId));
        } catch (Exception e) {
            log.error("Failed to get task with id: {}", taskId, e);
            throw new RuntimeException("Failed to get task due to database error", e);
        }
    }

    public Task updateTask(UUID taskId, Task task) {
        try {
            if (taskId == null) {
                throw new InvalidTaskException("Task ID cannot be null");
            }
            validateTask(task);
            UUID currentUserId = getCurrentUserId();
            
            // Verify task exists and belongs to current user
            Task existingTask = taskRepository.findById(taskId, currentUserId)
                    .orElseThrow(() -> new TaskNotFoundException(taskId));
            
            Task updatedTask = new Task(
                    existingTask.id(),
                    task.name(),
                    task.description(),
                    task.dueDate(),
                    task.status(),
                    task.priority(),
                    task.tags(),
                    existingTask.ownerId() // Preserve the original owner
            );
            
            return taskRepository.save(updatedTask);
        } catch (Exception e) {
            log.error("Failed to update task with id: {}", taskId, e);
            throw new RuntimeException("Failed to update task due to database error", e);
        }
    }

    public void deleteTask(UUID taskId) {
        try {
            if (taskId == null) {
                throw new InvalidTaskException("Task ID cannot be null");
            }
            UUID currentUserId = getCurrentUserId();
            
            // Verify task exists and belongs to current user
            taskRepository.findById(taskId, currentUserId)
                    .orElseThrow(() -> new TaskNotFoundException(taskId));
            
            taskRepository.deleteById(taskId, currentUserId);
        } catch (Exception e) {
            log.error("Failed to delete task with id: {}", taskId, e);
            throw new RuntimeException("Failed to delete task due to database error", e);
        }
    }

    public Page<Task> findTasks(
            String search,
            TaskStatus status,
            TaskPriority priority,
            String tag,
            int page,
            int size,
            SortOption sortOption
    ) {
        try {
            UUID currentUserId = getCurrentUserId();
            return taskRepository.findTasks(
                    currentUserId,
                    search,
                    status,
                    priority,
                    tag,
                    page,
                    size,
                    sortOption
            );
        } catch (Exception e) {
            log.error("Failed to find tasks with filters: search={}, status={}, priority={}, tag={}", search, status, priority, tag, e);
            throw new RuntimeException("Failed to find tasks due to database error", e);
        }
    }

    private void validateCreateTaskRequest(CreateTaskRequest request, String requestId) {
        if (request == null) {
            throw new InvalidTaskException("Task request cannot be null");
        }
        if (requestId == null || requestId.trim().isEmpty()) {
            throw new InvalidTaskException("Request ID cannot be null or empty");
        }
        if (request.name() == null || request.name().trim().isEmpty()) {
            throw new InvalidTaskException("Task name cannot be null or empty");
        }
        if (request.priority() == null) {
            throw new InvalidTaskException("Task priority cannot be null");
        }
        if (request.tags() == null) {
            throw new InvalidTaskException("Task tags cannot be null");
        }
    }

    private void validateTask(Task task) {
        if (task == null) {
            throw new InvalidTaskException("Task cannot be null");
        }
        if (task.name() == null || task.name().trim().isEmpty()) {
            throw new InvalidTaskException("Task name cannot be null or empty");
        }
        if (task.status() == null) {
            throw new InvalidTaskException("Task status cannot be null");
        }
        if (task.priority() == null) {
            throw new InvalidTaskException("Task priority cannot be null");
        }
        if (task.tags() == null) {
            throw new InvalidTaskException("Task tags cannot be null");
        }
        if (task.ownerId() == null) {
            throw new InvalidTaskException("Task owner ID cannot be null");
        }
    }
}
