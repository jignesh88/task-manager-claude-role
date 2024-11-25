package com.grabduck.taskmanager.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.grabduck.taskmanager.domain.Task;
import com.grabduck.taskmanager.domain.TaskPriority;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class TaskDataGenerator {
    private static final int NUMBER_OF_TASKS = 500;
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private static final String[] TASK_PREFIXES = {
            "Review", "Update", "Create", "Delete", "Refactor", "Implement", "Test",
            "Debug", "Deploy", "Monitor", "Optimize", "Configure", "Analyze", "Document"
    };

    private static final String[] TASK_SUBJECTS = {
            "API endpoint", "database schema", "user interface", "authentication system",
            "caching mechanism", "logging system", "error handling", "unit tests",
            "integration tests", "deployment pipeline", "documentation", "security features",
            "performance metrics", "backup system"
    };

    private static final String[] TASK_DESCRIPTIONS = {
            "This task requires careful attention to detail and thorough testing.",
            "High priority task that needs immediate attention.",
            "Regular maintenance task that should be completed soon.",
            "Important feature that will improve system performance.",
            "Critical security update that needs to be implemented.",
            "Documentation update to reflect recent changes.",
            "Bug fix that addresses reported user issues.",
            "Performance optimization for better user experience.",
            "Code cleanup to improve maintainability.",
            "Feature enhancement based on user feedback."
    };

    private static final String[] TAGS = {
            "backend", "frontend", "database", "security", "performance",
            "bug", "feature", "documentation", "testing", "devops",
            "ui", "ux", "api", "maintenance", "optimization",
            "urgent", "low-priority", "medium-priority", "high-priority", "critical"
    };

    public static void main(String[] args) {
        try {
            List<Task> tasks = generateTasks();
            saveTasksToJson(tasks);
        } catch (IOException e) {
            log.error("Failed to generate and save tasks", e);
        }
    }

    private static List<Task> generateTasks() {
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_TASKS; i++) {
            tasks.add(generateRandomTask());
            if ((i + 1) % 100 == 0) {
                log.info("Generated {} tasks", i + 1);
            }
        }
        return tasks;
    }

    private static void saveTasksToJson(List<Task> tasks) throws IOException {
        Path resourcePath = Paths.get("src", "main", "resources", "tasks.json");
        File file = resourcePath.toFile();
        file.getParentFile().mkdirs();
        objectMapper.writeValue(file, tasks);
        log.info("Saved {} tasks to {}", tasks.size(), resourcePath);
    }

    private static Task generateRandomTask() {
        String name = generateTaskName();
        String description = TASK_DESCRIPTIONS[ThreadLocalRandom.current().nextInt(TASK_DESCRIPTIONS.length)];
        LocalDateTime dueDate = generateRandomDueDate();
        TaskPriority priority = generateRandomPriority();
        Set<String> taskTags = generateRandomTags();

        return Task.createNew(name, description, dueDate, priority, taskTags);
    }

    private static String generateTaskName() {
        String prefix = TASK_PREFIXES[ThreadLocalRandom.current().nextInt(TASK_PREFIXES.length)];
        String subject = TASK_SUBJECTS[ThreadLocalRandom.current().nextInt(TASK_SUBJECTS.length)];
        return prefix + " " + subject;
    }

    private static LocalDateTime generateRandomDueDate() {
        LocalDateTime now = LocalDateTime.now();
        long minDay = now.plusDays(1).toLocalDate().toEpochDay();
        long maxDay = now.plusMonths(6).toLocalDate().toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
        
        return LocalDateTime.of(
                LocalDate.ofEpochDay(randomDay),
                now.toLocalTime()
        );
    }

    private static TaskPriority generateRandomPriority() {
        return TaskPriority.values()[ThreadLocalRandom.current().nextInt(TaskPriority.values().length)];
    }

    private static Set<String> generateRandomTags() {
        int numberOfTags = ThreadLocalRandom.current().nextInt(2, 5); // 2-4 tags per task
        Set<String> taskTags = new HashSet<>();
        
        while (taskTags.size() < numberOfTags) {
            taskTags.add(TAGS[ThreadLocalRandom.current().nextInt(TAGS.length)]);
        }
        
        return taskTags;
    }
}
