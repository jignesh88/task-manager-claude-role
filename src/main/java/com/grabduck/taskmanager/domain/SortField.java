package com.grabduck.taskmanager.domain;

import java.util.Arrays;

/**
 * Enumeration of fields that can be used for sorting tasks.
 */
public enum SortField {
    DUE_DATE("dueDate"),
    NAME("name"),
    STATUS("status"),
    PRIORITY("priority");

    private final String fieldName;

    SortField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    /**
     * Creates a SortField from its string representation.
     *
     * @param field the field name as string
     * @return the corresponding SortField
     * @throws IllegalArgumentException if the field name is invalid
     */
    public static SortField fromString(String field) {
        return Arrays.stream(values())
                .filter(value -> value.fieldName.equalsIgnoreCase(field))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown sort field: " + field + ". Valid values are: " +
                                Arrays.toString(Arrays.stream(values())
                                        .map(SortField::getFieldName)
                                        .toArray())
                ));
    }
}
