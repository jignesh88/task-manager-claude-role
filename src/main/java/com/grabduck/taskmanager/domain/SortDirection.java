package com.grabduck.taskmanager.domain;

import java.util.Arrays;

/**
 * Enumeration of possible sort directions.
 */
public enum SortDirection {
    ASC("asc"),
    DESC("desc");

    private final String direction;

    SortDirection(String direction) {
        this.direction = direction;
    }

    public String getDirection() {
        return direction;
    }

    /**
     * Creates a SortDirection from its string representation.
     *
     * @param direction the direction as string
     * @return the corresponding SortDirection
     * @throws IllegalArgumentException if the direction is invalid
     */
    public static SortDirection fromString(String direction) {
        return Arrays.stream(values())
                .filter(value -> value.direction.equalsIgnoreCase(direction))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown sort direction: " + direction + ". Valid values are: " +
                                Arrays.toString(Arrays.stream(values())
                                        .map(SortDirection::getDirection)
                                        .toArray())
                ));
    }
}
