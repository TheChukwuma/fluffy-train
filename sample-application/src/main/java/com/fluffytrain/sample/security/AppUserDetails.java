package com.fluffytrain.sample.security;

import com.fluffytrain.security.core.jwt.UserDetailsWithId;
import com.fluffytrain.sample.domain.Role;
import com.fluffytrain.sample.domain.UserAccount;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.stream.Collectors;

public final class AppUserDetails implements UserDetailsWithId {

    private final UserAccount user;

    public AppUserDetails(UserAccount user) {
        this.user = user;
    }

    @Override
    public String getUserId() {
        return String.valueOf(user.getId());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(Role::name)
                .map(r -> "ROLE_" + r)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
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
