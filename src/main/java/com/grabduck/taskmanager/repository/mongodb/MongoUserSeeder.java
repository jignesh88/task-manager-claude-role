package com.grabduck.taskmanager.repository.mongodb;

import com.grabduck.taskmanager.domain.User;
import com.grabduck.taskmanager.domain.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class MongoUserSeeder {

    private final MongoUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void seed() {
        if (userRepository.findByUsername("user-new").isEmpty()) {
            log.info("Seeding new user: user-new");
            
            var user = User.createNew(
                "user-new",
                "user-new@example.com",
                passwordEncoder.encode("123"),
                Set.of(UserRole.USER)
            );
            
            userRepository.save(user);
            log.info("User seeded successfully");
        } else {
            log.info("User 'user-new' already exists, skipping seeding");
        }
    }
}
