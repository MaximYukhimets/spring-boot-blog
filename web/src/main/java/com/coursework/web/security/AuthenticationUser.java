package com.coursework.web.security;

import com.coursework.domain.entity.Role;
import com.coursework.domain.entity.User;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ToString
public class AuthenticationUser implements UserDetails {

    private final long id;
    private final String username;
    private final String password;
    private final String email;
    private final boolean isActive;
    private final Set<? extends GrantedAuthority> authorities;

    public AuthenticationUser(long id, String username, String password, String email, boolean isActive, Set<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.isActive = isActive;
        this.authorities = authorities;
    }

    public static UserDetails fromUser(User user) {
        return new AuthenticationUser(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.isActive(),
                RoleToAuthorities(user.getRoles())
        );
    }

    public static Set<SimpleGrantedAuthority> RoleToAuthorities(Set<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase()))
                .collect(Collectors.toSet());
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
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

}

