package com.grabduck.taskmanager.repository.mongodb;

import com.grabduck.taskmanager.domain.Task;
import com.grabduck.taskmanager.repository.TaskDataProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MongoTaskSeeder {

    private final MongoTaskRepository taskRepository;
    private final TaskDataProvider taskDataProvider;

    @EventListener(ApplicationReadyEvent.class)
    public void seed() {
        if (isSeeded()) {
            log.info("Database already seeded, skipping seeding step");
            return;
        }

        log.info("Starting database seeding...");
        var sampleTasks = taskDataProvider.getTasks();
        
        for (Task task : sampleTasks) {
            taskRepository.save(task);
        }
        
        log.info("Successfully seeded {} tasks into MongoDB", sampleTasks.size());
    }

    private boolean isSeeded() {
        return taskRepository.count() > 0;
    }
}
