package com.grabduck.taskmanager.repository.mongodb.migration;

import com.grabduck.taskmanager.repository.UserRepository;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Migration script to add owner_id field to existing tasks.
 * This will run once when the application starts and assign all existing tasks
 * to the first available user in the system.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskOwnerMigration {
    private static final String MIGRATION_ID = "ADD_OWNER_TO_TASKS_001";
    
    private final MongoTemplate mongoTemplate;
    private final UserRepository userRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void migrate() {
        MongoDatabase database = mongoTemplate.getDb();
        MongoCollection<Document> migrationCollection = database.getCollection("migrations");

        // Check if migration was already applied
        if (isMigrationApplied(migrationCollection)) {
            log.info("Migration {} already applied, skipping", MIGRATION_ID);
            return;
        }

        try {
            // Get the first user from the system to assign tasks to
            String defaultOwnerId = userRepository.findAllUsers()
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No users found in the system"))
                .id()
                .toString();

            // Update all tasks that don't have an owner
            MongoCollection<Document> tasksCollection = database.getCollection("tasks");
            tasksCollection.updateMany(
                new Document("owner_id", new Document("$exists", false)),
                Updates.set("owner_id", defaultOwnerId)
            );

            // Record migration as completed
            recordMigration(migrationCollection);
            log.info("Successfully migrated tasks to include owner_id");
        } catch (Exception e) {
            log.error("Failed to migrate tasks", e);
            throw new RuntimeException("Failed to migrate tasks", e);
        }
    }

    private boolean isMigrationApplied(MongoCollection<Document> collection) {
        return collection.countDocuments(new Document("_id", MIGRATION_ID)) > 0;
    }

    private void recordMigration(MongoCollection<Document> collection) {
        Document migration = new Document("_id", MIGRATION_ID)
            .append("appliedAt", System.currentTimeMillis());
        collection.insertOne(migration);
    }
}
