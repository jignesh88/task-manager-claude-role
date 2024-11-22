package com.grabduck.taskmanager.dto;

import com.grabduck.taskmanager.domain.Page;

import java.util.List;

/**
 * Web layer DTO for paginated responses.
 * Maps from the domain layer Page model to a format suitable for REST responses.
 *
 * @param <T> The type of elements in the page
 */
public record PagedResponseDto<T>(
        List<T> content,
        long totalElements,
        int totalPages,
        int size,
        int page
) {
    /**
     * Creates a PagedResponseDto from a domain layer Page model.
     *
     * @param page The domain layer page
     * @param <T> The type of elements
     * @return A new PagedResponseDto
     */
    public static <T> PagedResponseDto<T> from(Page<T> page) {
        return new PagedResponseDto<>(
                page.elements(),
                page.totalElements(),
                page.totalPages(),
                page.pageSize(),
                page.pageNumber()
        );
    }
}
