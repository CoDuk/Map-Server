package com.coduk.duksungmap.global.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class SecurityUserPrincipal implements UserDetails {

    private final Long userId;
    private final boolean isAdmin;

    private SecurityUserPrincipal(Long userId, boolean isAdmin) {
        this.userId = userId;
        this.isAdmin = isAdmin;
    }

    public static SecurityUserPrincipal of(Long userId, boolean isAdmin) {
        return new SecurityUserPrincipal(userId, isAdmin);
    }

    public Long userId() {
        return userId;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (isAdmin) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        }
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override public String getPassword() { return null; }
    @Override public String getUsername() { return String.valueOf(userId); }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}