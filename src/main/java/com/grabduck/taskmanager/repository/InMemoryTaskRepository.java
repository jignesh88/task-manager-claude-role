package com.grabduck.taskmanager.repository;

import com.grabduck.taskmanager.domain.Task;
import com.grabduck.taskmanager.domain.TaskPriority;
import com.grabduck.taskmanager.domain.TaskStatus;
import com.grabduck.taskmanager.domain.Page;
import com.grabduck.taskmanager.domain.SortDirection;
import com.grabduck.taskmanager.domain.SortOption;
import com.grabduck.taskmanager.domain.SortField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class InMemoryTaskRepository implements TaskRepository {
    private final Map<UUID, Task> tasks = new HashMap<>();

    @Override
    public Task save(Task task) {
        log.info("Saving task: {}", task);
        tasks.put(task.id(), task);
        log.info("Task saved successfully");
        return task;
    }

    @Override
    public Optional<Task> findById(UUID id, UUID ownerId) {
        log.info("Finding task by id: {} and ownerId: {}", id, ownerId);
        Task task = tasks.get(id);
        if (task == null || (ownerId != null && !ownerId.equals(task.ownerId()))) {
            return Optional.empty();
        }
        return Optional.of(task);
    }

    @Override
    public void deleteById(UUID id, UUID ownerId) {
        log.info("Deleting task by id: {} and ownerId: {}", id, ownerId);
        Task task = tasks.get(id);
        if (task != null && (ownerId == null || ownerId.equals(task.ownerId()))) {
            tasks.remove(id);
        }
    }

    @Override
    public Page<Task> findTasks(
            UUID ownerId,
            String search,
            TaskStatus status,
            TaskPriority priority,
            String tag,
            int page,
            int size,
            SortOption sortOption
    ) {
        log.info("Finding tasks with filters: ownerId={}, search={}, status={}, priority={}, tag={}", 
                ownerId, search, status, priority, tag);

        List<Task> filteredTasks = tasks.values().stream()
                .filter(task -> ownerId == null || ownerId.equals(task.ownerId()))
                .filter(task -> search == null || 
                        task.name().toLowerCase().contains(search.toLowerCase()) ||
                        task.description().toLowerCase().contains(search.toLowerCase()))
                .filter(task -> status == null || status.equals(task.status()))
                .filter(task -> priority == null || priority.equals(task.priority()))
                .filter(task -> tag == null || task.tags().contains(tag))
                .sorted((t1, t2) -> {
                    int direction = sortOption.direction().equals(SortDirection.ASC) ? 1 : -1;
                    switch (sortOption.field()) {
                        case DUE_DATE:
                            return direction * Objects.compare(t1.dueDate(), t2.dueDate(), Comparator.nullsLast(Comparator.naturalOrder()));
                        case NAME:
                            return direction * t1.name().compareTo(t2.name());
                        case STATUS:
                            return direction * t1.status().compareTo(t2.status());
                        case PRIORITY:
                            return direction * t1.priority().compareTo(t2.priority());
                        default:
                            return 0;
                    }
                })
                .collect(Collectors.toList());

        int start = page * size;
        int end = Math.min(start + size, filteredTasks.size());
        List<Task> pagedTasks = start < filteredTasks.size() ? 
                filteredTasks.subList(start, end) : 
                Collections.emptyList();

        return new Page<>(
                pagedTasks,
                filteredTasks.size(),
                (int) Math.ceil((double) filteredTasks.size() / size),
                size,
                page
        );
    }
}
