package com.grabduck.taskmanager.security;

import com.grabduck.taskmanager.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public record JwtUserDetails(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities, boolean active) implements UserDetails {

    public JwtUserDetails(User user) {
        this(user.id().getMostSignificantBits(), user.username(), user.password(), Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")), true);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return active;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return active;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
