package com.grabduck.taskmanager.config;

import com.grabduck.taskmanager.security.JwtTokenUtil;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;

@TestConfiguration
public class TestJwtConfig {

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @Bean
    public JwtProperties jwtProperties() {
        JwtProperties properties = new JwtProperties();
        properties.setSecretKey("testSecretKeyWithAtLeast256BitsForHMACSHA256Algorithm");
        properties.setExpirationMs(3600000L); // 1 hour
        properties.setIssuer("test-issuer");
        return properties;
    }
}
