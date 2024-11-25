package com.grabduck.taskmanager.repository.mongodb;

import com.grabduck.taskmanager.domain.Task;
import com.grabduck.taskmanager.domain.TaskPriority;
import com.grabduck.taskmanager.domain.TaskStatus;
import com.grabduck.taskmanager.domain.Page;
import com.grabduck.taskmanager.domain.SortOption;
import com.grabduck.taskmanager.domain.SortDirection;
import com.grabduck.taskmanager.repository.TaskRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@Primary
public interface MongoTaskRepository extends MongoRepository<TaskDocument, String>, TaskRepository {
    
    // Using Spring Data MongoDB's method query instead of @Query
    org.springframework.data.domain.Page<TaskDocument> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStatusAndPriorityAndTagsContaining(
            String name, String description,
            TaskStatus status,
            TaskPriority priority,
            String tag,
            org.springframework.data.domain.Pageable pageable
    );

    @Override
    default Task save(Task task) {
        TaskDocument document = new TaskDocument(task);
        return save(document).toDomainTask();
    }

    @Override
    default Optional<Task> findById(UUID id) {
        return findById(id.toString())
                .map(TaskDocument::toDomainTask);
    }

    @Override
    default void deleteById(UUID id) {
        deleteById(id.toString());
    }

    @Override
    default Page<Task> findTasks(
            String search,
            TaskStatus status,
            TaskPriority priority,
            String tag,
            int page,
            int size,
            SortOption sortOption
    ) {
        Sort sort = createSort(sortOption);
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        org.springframework.data.domain.Page<TaskDocument> result;
        if (search == null && status == null && priority == null && tag == null) {
            result = findAll(pageRequest);
        } else {
            result = findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStatusAndPriorityAndTagsContaining(
                    search != null ? search : "",
                    search != null ? search : "",
                    status,
                    priority,
                    tag,
                    pageRequest
            );
        }

        return new Page<>(
                result.getContent().stream()
                        .map(TaskDocument::toDomainTask)
                        .toList(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.getSize(),
                result.getNumber()
        );
    }

    private static Sort createSort(SortOption sortOption) {
        Sort.Direction direction = sortOption.direction() == SortDirection.ASC ? 
            Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, sortOption.field().getFieldName());
    }
}
