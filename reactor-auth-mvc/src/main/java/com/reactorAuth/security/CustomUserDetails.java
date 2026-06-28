package com.reactorAuth.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class CustomUserDetails implements UserDetails {
    private final Long userId;
    private final String username;
    private final String password;
    private final String email;
    private final String fullName;
    private final Set<String> roles;
    private final Set<String> permissions;
    private final boolean enabled;

    public CustomUserDetails(Long userId, String username, String password, String email,
                             String fullName, Set<String> roles, Set<String> permissions, boolean enabled) {
        this.userId = userId; this.username = username; this.password = password;
        this.email = email; this.fullName = fullName; this.roles = roles;
        this.permissions = permissions; this.enabled = enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Stream.concat(
                roles.stream().map(SimpleGrantedAuthority::new),
                permissions.stream().map(SimpleGrantedAuthority::new)
        ).collect(Collectors.toSet());
    }

    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return username; }
    @Override public boolean isEnabled() { return enabled; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
}
