package com.grabduck.taskmanager.domain;

import lombok.NonNull;
import java.util.Set;
import java.util.UUID;

public record User(
    @NonNull UUID id,
    @NonNull String username,
    @NonNull String email,
    @NonNull String password,
    @NonNull Set<UserRole> roles
) {
    // Ensure roles are immutable
    public User {
        roles = Set.copyOf(roles);
    }

    // Factory method for creating a new user
    public static User createNew(
            @NonNull String username,
            @NonNull String email,
            @NonNull String password,
            @NonNull Set<UserRole> roles) {
        return new User(
            UUID.randomUUID(),
            username,
            email,
            password,
            roles
        );
    }
}
