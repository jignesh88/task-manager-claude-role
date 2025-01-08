package com.grabduck.taskmanager.security;

import com.grabduck.taskmanager.config.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenUtilTest {

    private JwtTokenUtil jwtTokenUtil;
    private JwtProperties jwtProperties;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
        jwtProperties.setSecretKey("test-secret-key-that-is-long-enough-for-testing-purposes");
        jwtProperties.setExpirationMs(3600000L); // 1 hour
        jwtProperties.setIssuer("test-issuer");

        jwtTokenUtil = new JwtTokenUtil(jwtProperties);
        userDetails = new User("testuser", "password", Collections.emptyList());
    }

    @Test
    void whenGenerateToken_thenTokenIsValid() {
        String token = jwtTokenUtil.generateToken(userDetails);
        
        assertNotNull(token);
        assertTrue(jwtTokenUtil.isTokenValid(token, userDetails));
    }

    @Test
    void whenGenerateTokenWithExtraClaims_thenClaimsArePresent() {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "ADMIN");
        
        String token = jwtTokenUtil.generateToken(extraClaims, userDetails);
        
        assertNotNull(token);
        assertEquals("ADMIN", jwtTokenUtil.extractClaim(token, claims -> claims.get("role")));
    }

    @Test
    void whenExtractUsername_thenUsernameMatches() {
        String token = jwtTokenUtil.generateToken(userDetails);
        
        String username = jwtTokenUtil.extractUsername(token);
        
        assertEquals(userDetails.getUsername(), username);
    }

    @Test
    void whenTokenNotExpired_thenTokenIsValid() {
        String token = jwtTokenUtil.generateToken(userDetails);
        
        assertTrue(jwtTokenUtil.isTokenValid(token, userDetails));
    }

    @Test
    void whenDifferentUser_thenTokenIsInvalid() {
        String token = jwtTokenUtil.generateToken(userDetails);
        UserDetails differentUser = new User("different", "password", Collections.emptyList());
        
        assertFalse(jwtTokenUtil.isTokenValid(token, differentUser));
    }
}
