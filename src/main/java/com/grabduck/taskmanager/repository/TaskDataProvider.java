package com.grabduck.taskmanager.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.grabduck.taskmanager.domain.Task;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class TaskDataProvider {
    private static final String TASKS_JSON_PATH = "tasks.json";
    private final ObjectMapper objectMapper;
    private List<Task> tasks;

    public TaskDataProvider() {
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
        this.tasks = Collections.emptyList();
    }

    @PostConstruct
    public void init() {
        loadTasks();
    }

    private void loadTasks() {
        try {
            ClassPathResource resource = new ClassPathResource(TASKS_JSON_PATH);
            try (InputStream inputStream = resource.getInputStream()) {
                List<Task> loadedTasks = objectMapper.readValue(inputStream, new TypeReference<List<Task>>() {});
                tasks = Collections.unmodifiableList(loadedTasks);
                log.info("Successfully loaded {} tasks from {}", tasks.size(), TASKS_JSON_PATH);
            }
        } catch (IOException e) {
            log.error("Failed to load tasks from {}", TASKS_JSON_PATH, e);
            tasks = Collections.emptyList();
        }
    }

    public List<Task> getTasks() {
        return tasks;
    }
}
