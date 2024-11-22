package com.grabduck.taskmanager.controller;

import com.grabduck.taskmanager.domain.Task;
import com.grabduck.taskmanager.domain.TaskPriority;
import com.grabduck.taskmanager.domain.TaskStatus;
import com.grabduck.taskmanager.dto.PagedResponseDto;
import com.grabduck.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for managing tasks in the Task Manager application.
 * Provides endpoints for creating, retrieving, updating, and deleting tasks,
 * as well as searching and filtering tasks with pagination support.
 *
 * @see Task
 * @see TaskService
 */
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /**
     * Creates a new task.
     *
     * @param task The task to create. Must not be null and must contain required fields (name, tags).
     * @return ResponseEntity containing the created task with HTTP status 201 (Created)
     * @throws com.grabduck.taskmanager.exception.InvalidTaskException if the task is invalid
     */
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(task));
    }

    /**
     * Retrieves a paginated list of tasks with optional filtering and search capabilities.
     *
     * @param search Optional search term to filter tasks by name or description
     * @param status Optional status filter
     * @param priority Optional priority filter
     * @param tag Optional tag filter
     * @param page Page number (0-based). Must be non-negative.
     * @param size Number of items per page. Must be between 1 and 100.
     * @param sort Optional sort criteria (format: "field,direction", e.g., "dueDate,desc")
     * @return ResponseEntity containing a paginated list of tasks matching the criteria
     * @throws com.grabduck.taskmanager.exception.InvalidTaskException if pagination parameters are invalid
     */
    @GetMapping
    public ResponseEntity<PagedResponseDto<Task>> getTasks(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dueDate,asc") String sort
    ) {
        return ResponseEntity.ok(PagedResponseDto.from(
                taskService.getTasks(search, status, priority, tag, page, size, sort)
        ));
    }

    /**
     * Retrieves a specific task by its ID.
     *
     * @param taskId The UUID of the task to retrieve
     * @return ResponseEntity containing the task if found
     * @throws com.grabduck.taskmanager.exception.TaskNotFoundException if the task is not found
     * @throws com.grabduck.taskmanager.exception.InvalidTaskException if the taskId is null
     */
    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getTaskById(@PathVariable UUID taskId) {
        return ResponseEntity.ok(taskService.getTaskById(taskId));
    }

    /**
     * Updates an existing task.
     *
     * @param taskId The UUID of the task to update
     * @param task The updated task data. Must not be null and must contain required fields.
     * @return ResponseEntity containing the updated task
     * @throws com.grabduck.taskmanager.exception.TaskNotFoundException if the task is not found
     * @throws com.grabduck.taskmanager.exception.InvalidTaskException if the task data is invalid
     */
    @PutMapping("/{taskId}")
    public ResponseEntity<Task> updateTask(
            @PathVariable UUID taskId,
            @RequestBody Task task
    ) {
        return ResponseEntity.ok(taskService.updateTask(taskId, task));
    }

    /**
     * Deletes a task by its ID.
     *
     * @param taskId The UUID of the task to delete
     * @return ResponseEntity with no content and HTTP status 204 (No Content)
     * @throws com.grabduck.taskmanager.exception.TaskNotFoundException if the task is not found
     * @throws com.grabduck.taskmanager.exception.InvalidTaskException if the taskId is null
     */
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}
