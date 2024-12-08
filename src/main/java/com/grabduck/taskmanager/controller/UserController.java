package com.grabduck.taskmanager.controller;

import com.grabduck.taskmanager.domain.User;
import com.grabduck.taskmanager.dto.UserDto;
import com.grabduck.taskmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing users in the Task Manager application.
 * Provides endpoints for user registration and management.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Creates a new user in the system.
     *
     * @param userDto The user creation request containing user details
     * @return ResponseEntity containing the created user information
     */
    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        User user = userService.createUser(userDto.toDomain());
        return ResponseEntity.ok(UserDto.from(user));
    }
}
