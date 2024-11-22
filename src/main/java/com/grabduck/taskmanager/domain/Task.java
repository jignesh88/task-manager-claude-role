package com.grabduck.taskmanager.domain;

import lombok.NonNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record Task(
    @NonNull UUID id,
    @NonNull String name,
    String description,
    LocalDateTime dueDate,
    @NonNull TaskStatus status,
    @NonNull TaskPriority priority,
    @NonNull List<String> tags
) {
    public Task {
        tags = List.copyOf(tags); // Make tags immutable
    }

    // Factory method for creating a new task
    public static Task createNew(
            @NonNull String name,
            String description,
            LocalDateTime dueDate,
            @NonNull TaskPriority priority,
            @NonNull List<String> tags) {
        return new Task(
            UUID.randomUUID(),
            name,
            description,
            dueDate,
            TaskStatus.NOT_STARTED,
            priority,
            tags
        );
    }
}
