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
    Optional<Task> findById(UUID id);
    void deleteById(UUID id);
    Page<Task> findTasks(
            String search,
            TaskStatus status,
            TaskPriority priority,
            String tag,
            int page,
            int size,
            SortOption sortOption
    );
}
