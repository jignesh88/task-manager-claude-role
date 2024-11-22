package com.grabduck.taskmanager.repository;

import com.grabduck.taskmanager.domain.Task;
import com.grabduck.taskmanager.domain.TaskPriority;
import com.grabduck.taskmanager.domain.TaskStatus;
import com.grabduck.taskmanager.domain.Page;
import com.grabduck.taskmanager.domain.SortOption;
import com.grabduck.taskmanager.domain.SortDirection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Slf4j
public class InMemoryTaskRepository implements TaskRepository {
    private final Map<UUID, Task> tasks = new ConcurrentHashMap<>();

    private static final Task DUMMY_TASK = Task.createNew(
            "Sample Task",
            "This is a sample task description",
            LocalDateTime.now().plusDays(7),
            TaskPriority.HIGH,
            Set.of("sample", "dummy")
    );

    public InMemoryTaskRepository() {
        // Initialize with some dummy data
        Task task1 = DUMMY_TASK;
        Task task2 = Task.createNew(
                "Another Task",
                "Another task description",
                LocalDateTime.now().plusDays(3),
                TaskPriority.MEDIUM,
                Set.of("work", "important")
        );
        Task task3 = Task.createNew(
                "Urgent Task",
                "This needs immediate attention",
                LocalDateTime.now().plusDays(1),
                TaskPriority.URGENT,
                Set.of("urgent", "critical")
        );

        tasks.put(task1.id(), task1);
        tasks.put(task2.id(), task2);
        tasks.put(task3.id(), task3);
    }

    @Override
    public Task save(Task task) {
        log.debug("Saving task with ID: {}", task.id());
        tasks.put(task.id(), task);
        return task;
    }

    @Override
    public Optional<Task> findById(UUID id) {
        log.debug("Finding task by ID: {}", id);
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public void deleteById(UUID id) {
        log.debug("Deleting task by ID: {}", id);
        tasks.remove(id);
    }

    @Override
    public Page<Task> findTasks(
            String search,
            TaskStatus status,
            TaskPriority priority,
            String tag,
            int page,
            int size,
            SortOption sortOption
    ) {
        log.debug("Finding tasks with search: {}, status: {}, priority: {}, tag: {}, page: {}, size: {}, sortOption: {}",
                search, status, priority, tag, page, size, sortOption);
        List<Task> filteredTasks = tasks.values().stream()
                .filter(task -> matchesSearch(task, search))
                .filter(task -> matchesStatus(task, status))
                .filter(task -> matchesPriority(task, priority))
                .filter(task -> matchesTag(task, tag))
                .toList();

        // Apply sorting
        List<Task> sortedTasks = applySorting(filteredTasks, sortOption);

        // Apply pagination
        int start = page * size;
        int end = Math.min(start + size, sortedTasks.size());
        List<Task> pagedTasks = start < sortedTasks.size() 
                ? sortedTasks.subList(start, end)
                : Collections.emptyList();

        return new Page<>(
                pagedTasks,
                filteredTasks.size(),
                (int) Math.ceil((double) filteredTasks.size() / size),
                size,
                page
        );
    }

    private boolean matchesSearch(Task task, String search) {
        if (search == null || search.trim().isEmpty()) {
            return true;
        }
        String searchLower = search.toLowerCase();
        return task.name().toLowerCase().contains(searchLower) ||
                (task.description() != null && task.description().toLowerCase().contains(searchLower));
    }

    private boolean matchesStatus(Task task, TaskStatus status) {
        return status == null || task.status() == status;
    }

    private boolean matchesPriority(Task task, TaskPriority priority) {
        return priority == null || task.priority() == priority;
    }

    private boolean matchesTag(Task task, String tag) {
        return tag == null || tag.trim().isEmpty() || task.tags().contains(tag);
    }

    private List<Task> applySorting(List<Task> tasks, SortOption sortOption) {
        if (sortOption == null) {
            return tasks;
        }

        Comparator<Task> comparator = switch (sortOption.field()) {
            case DUE_DATE -> Comparator.comparing(Task::dueDate, Comparator.nullsLast(Comparator.naturalOrder()));
            case NAME -> Comparator.comparing(Task::name);
            case STATUS -> Comparator.comparing(Task::status);
            case PRIORITY -> Comparator.comparing(Task::priority);
        };

        return tasks.stream()
                .sorted(sortOption.direction() == SortDirection.DESC ? comparator.reversed() : comparator)
                .toList();
    }
}
