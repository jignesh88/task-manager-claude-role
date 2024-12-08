package com.grabduck.taskmanager.service;

import com.grabduck.taskmanager.domain.User;
import com.grabduck.taskmanager.exception.UserRegistrationException;
import com.grabduck.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates a new user with validation.
     * @param user The user to create
     * @return The created user
     * @throws UserRegistrationException if registration validation fails
     */
    public User createUser(User user) {
        // Validate username and email uniqueness
        if (userRepository.existsByUsername(user.username()) ||
            userRepository.existsByEmail(user.email())) {
            throw new UserRegistrationException("Invalid registration details");
        }

        // Create new user with encoded password
        return userRepository.save(
            user.withPassword(passwordEncoder.encode(user.password()))
        );
    }
}
