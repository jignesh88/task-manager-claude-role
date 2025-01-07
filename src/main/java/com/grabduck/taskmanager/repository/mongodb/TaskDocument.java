package com.grabduck.taskmanager.repository.mongodb;

import com.grabduck.taskmanager.domain.Task;
import com.grabduck.taskmanager.domain.TaskPriority;
import com.grabduck.taskmanager.domain.TaskStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Document(collection = "tasks")
@Data
@NoArgsConstructor
public class TaskDocument {
    @Id
    private String id;

    @Field("name")
    @Indexed
    private String name;

    @Field("description")
    private String description;

    @Field("due_date")
    @Indexed
    private LocalDateTime dueDate;

    @Field("status")
    @Indexed
    private TaskStatus status;

    @Field("priority")
    @Indexed
    private TaskPriority priority;

    @Field("tags")
    @Indexed
    private Set<String> tags;

    @Field("owner_id")
    @Indexed  // Add index for efficient querying by owner
    private String ownerId;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    public TaskDocument(Task task) {
        this.id = uuidToString(task.id());
        this.name = task.name();
        this.description = task.description();
        this.dueDate = task.dueDate();
        this.status = task.status();
        this.priority = task.priority();
        this.tags = Set.copyOf(task.tags());
        this.ownerId = uuidToString(task.ownerId());
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public Task toDomainTask() {
        return new Task(
            stringToUUID(id),
            name,
            description,
            dueDate,
            status,
            priority,
            tags,
            stringToUUID(ownerId)
        );
    }

    /**
     * Safely converts UUID to String, handling null values
     */
    private static String uuidToString(UUID uuid) {
        return uuid != null ? uuid.toString() : null;
    }

    /**
     * Safely converts String to UUID, handling null values
     */
    private static UUID stringToUUID(String str) {
        return str != null ? UUID.fromString(str) : null;
    }
}
