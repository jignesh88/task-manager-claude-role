package com.grabduck.taskmanager.dto;

import com.grabduck.taskmanager.domain.User;
import com.grabduck.taskmanager.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

/**
 * Data Transfer Object for User operations.
 * Used for both creating new users and returning user information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private UUID id;
    private String username;
    private String email;
    private String password;
    private Set<UserRole> roles;  // Only used for responses, ignored in requests

    /**
     * Creates a UserDto from a domain User model.
     * Note: Password is intentionally not included in the DTO for security.
     *
     * @param user The domain user model
     * @return A new UserDto
     */
    public static UserDto from(User user) {
        return UserDto.builder()
                .id(user.id())
                .username(user.username())
                .email(user.email())
                .roles(user.roles())
                .build();
    }

    /**
     * Converts this DTO to a domain User model.
     * Used when creating a new user.
     * Note: Roles in the request DTO are ignored, new users always get USER role.
     *
     * @return A new User domain model
     */
    public User toDomain() {
        return User.createNew(
                username,
                email,
                password,
                Set.of(UserRole.USER)  // Always assign USER role to new users
        );
    }
}
