package com.grabduck.taskmanager.repository.mongodb;

import com.grabduck.taskmanager.domain.User;
import com.grabduck.taskmanager.domain.UserRole;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Document(collection = "users")
@Data
@NoArgsConstructor
public class UserDocument {
    @Id
    private String id;

    @Field("username")
    @Indexed(unique = true)
    private String username;

    @Field("email")
    @Indexed(unique = true)
    private String email;

    @Field("password")
    private String password;

    @Field("roles")
    private Set<UserRole> roles;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    public UserDocument(User user) {
        this.id = user.id().toString();
        this.username = user.username();
        this.email = user.email();
        this.password = user.password();
        this.roles = user.roles();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public User toDomainUser() {
        return new User(
            UUID.fromString(id),
            username,
            email,
            password,
            roles
        );
    }
}
