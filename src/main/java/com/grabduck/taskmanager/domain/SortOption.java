package com.grabduck.taskmanager.domain;

/**
 * Represents sorting options for task queries.
 * This class encapsulates the field to sort by and the direction of sorting.
 */
public record SortOption(
        SortField field,
        SortDirection direction
) {
    /**
     * Creates a SortOption from a string representation (e.g., "dueDate,desc").
     *
     * @param sortString the string to parse in format "field,direction"
     * @return a new SortOption
     * @throws IllegalArgumentException if the sort string is invalid
     */
    public static SortOption fromString(String sortString) {
        if (sortString == null || sortString.isBlank()) {
            return new SortOption(SortField.DUE_DATE, SortDirection.ASC);
        }

        String[] parts = sortString.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Sort string must be in format 'field,direction'");
        }

        try {
            SortField field = SortField.fromString(parts[0].trim());
            SortDirection direction = SortDirection.valueOf(parts[1].trim().toUpperCase());
            return new SortOption(field, direction);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Unknown sort field")) {
                throw e;
            } else {
                throw new IllegalArgumentException("Unknown sort direction: " + parts[1].trim());
            }
        }
    }
}
