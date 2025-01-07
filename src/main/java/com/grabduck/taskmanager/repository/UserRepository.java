package com.grabduck.taskmanager.repository;

import com.grabduck.taskmanager.domain.User;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    void deleteById(UUID id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findAllUsers();
}
