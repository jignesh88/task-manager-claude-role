package com.grabduck.taskmanager.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenUtil jwtTokenUtil;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private static final String VALID_TOKEN = "valid.jwt.token";
    private static final String TEST_USERNAME = "testuser";

    @BeforeEach
    void setUp() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenUtil, userDetailsService);
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldNotAuthenticateWhenNoToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldAuthenticateWithValidToken() throws Exception {
        String bearerToken = "Bearer " + VALID_TOKEN;
        UserDetails userDetails = new JwtUserDetails(1L, TEST_USERNAME, "password", Collections.emptyList(), true);

        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtTokenUtil.extractUsername(VALID_TOKEN)).thenReturn(TEST_USERNAME);
        when(jwtTokenUtil.isTokenValid(eq(VALID_TOKEN), any(UserDetails.class))).thenReturn(true);
        when(userDetailsService.loadUserByUsername(TEST_USERNAME)).thenReturn(userDetails);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(TEST_USERNAME, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    void shouldNotAuthenticateWithInvalidToken() throws Exception {
        String bearerToken = "Bearer " + VALID_TOKEN;

        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtTokenUtil.extractUsername(VALID_TOKEN)).thenReturn(TEST_USERNAME);
        when(jwtTokenUtil.isTokenValid(eq(VALID_TOKEN), any())).thenReturn(false);
        when(userDetailsService.loadUserByUsername(TEST_USERNAME)).thenReturn(mock(UserDetails.class));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldHandleAuthenticationException() throws Exception {
        String bearerToken = "Bearer " + VALID_TOKEN;

        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtTokenUtil.extractUsername(VALID_TOKEN)).thenThrow(new RuntimeException("Token parsing failed"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
