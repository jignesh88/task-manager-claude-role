package com.grabduck.taskmanager.dto;

import com.grabduck.taskmanager.domain.TaskPriority;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Data Transfer Object (DTO) for creating a new task in the system.
 * This record encapsulates all the necessary information required to create a task,
 * separating the input data from the domain model.
 *
 * @param name        The name/title of the task. Must not be null.
 * @param description Optional description providing more details about the task.
 * @param dueDate     Optional deadline for the task completion.
 * @param priority    The priority level of the task. Must not be null.
 * @param tags        Set of tags associated with the task for categorization. Must not be null.
 * @param user        User associated with the task. Must not be null.
 *
 * @since 1.0
 */
public record CreateTaskRequest(
    @NonNull String name,
    String description,
    LocalDateTime dueDate,
    @NonNull TaskPriority priority,
    @NonNull Set<String> tags,
    @NonNull String user
) {}
