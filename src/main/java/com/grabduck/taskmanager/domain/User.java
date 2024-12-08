package com.grabduck.taskmanager.domain;

import lombok.NonNull;
import java.util.Set;
import java.util.UUID;

public record User(
    @NonNull UUID id,
    @NonNull String username,
    @NonNull String email,
    @NonNull String password,
    Set<UserRole> roles
) {
    // Ensure roles are immutable
    public User {
        roles = roles != null ? Set.copyOf(roles) : Set.of();
    }

    /**
     * Returns user roles or empty set if roles are not defined
     *
     * @return immutable set of user roles
     */
    @Override
    public Set<UserRole> roles() {
        return roles != null ? roles : Set.of();
    }

    /**
     * Returns a new User with the specified password
     *
     * @param newPassword the new password
     * @return a new User instance with the updated password
     */
    public User withPassword(@NonNull String newPassword) {
        return new User(id, username, email, newPassword, roles);
    }

    // Factory method for creating a new user
    public static User createNew(
            @NonNull String username,
            @NonNull String email,
            @NonNull String password,
            Set<UserRole> roles) {
        return new User(
            UUID.randomUUID(),
            username,
            email,
            password,
            roles
        );
    }
}
