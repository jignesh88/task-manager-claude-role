package com.grabduck.taskmanager.domain;

import java.util.List;

/**
 * Domain model for pagination.
 * This class represents a page of results in the domain layer,
 * independent of any presentation layer concerns.
 *
 * @param <T> The type of elements in the page
 */
public record Page<T>(
        List<T> elements,
        long totalElements,
        int totalPages,
        int pageSize,
        int pageNumber
) {}
