package com.grabduck.taskmanager.repository;

import com.grabduck.taskmanager.domain.Task;
import com.grabduck.taskmanager.domain.TaskPriority;
import com.grabduck.taskmanager.domain.TaskStatus;
import com.grabduck.taskmanager.domain.Page;
import com.grabduck.taskmanager.domain.SortOption;

import java.util.Optional;
import java.util.UUID;

public interface TaskRepository {
    Task save(Task task);
    /**
     * Find a task by ID. If ownerId is provided, only returns the task if it belongs to that owner.
     */
    Optional<Task> findById(UUID id, UUID ownerId);
    /**
     * Delete a task by ID. If ownerId is provided, only deletes the task if it belongs to that owner.
     */
    void deleteById(UUID id, UUID ownerId);

    /**
     * Find tasks with optional filtering and pagination.
     * If ownerId is provided, only tasks belonging to that owner will be returned.
     */
    Page<Task> findTasks(
            UUID ownerId,
            String search,
            TaskStatus status,
            TaskPriority priority,
            String tag,
            int page,
            int size,
            SortOption sortOption
    );
}
