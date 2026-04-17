package com.example.abac.security;

import com.example.abac.domain.user.Role;
import com.example.abac.domain.user.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserPrincipal implements UserDetails {

    private final Long userId;
    private final String username;
    private final String password;
    private final Role role;
    private final Long departmentId;

    public CustomUserPrincipal(Long userId, String username, String password, Role role, Long departmentId) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.departmentId = departmentId;
    }

    public static CustomUserPrincipal from(User user) {
        return new CustomUserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getRole(),
                user.getDepartmentId()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.authority()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
