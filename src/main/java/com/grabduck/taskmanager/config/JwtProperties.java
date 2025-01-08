package com.grabduck.taskmanager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {
    /**
     * Secret key used to sign JWT tokens
     */
    private String secretKey = "d3c59e8f-24e9-4b86-87f5-e51c89a54b1e";
    
    /**
     * Token expiration time in milliseconds (default: 24 hours)
     */
    private long expirationMs = 86400000; // 24 hours
    
    /**
     * Token issuer
     */
    private String issuer = "task-manager";
}
