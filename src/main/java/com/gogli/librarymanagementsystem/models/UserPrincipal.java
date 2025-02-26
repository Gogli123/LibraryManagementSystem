package com.gogli.librarymanagementsystem.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserPrincipal implements UserDetails {

    private final Librarian librarian;

    public UserPrincipal(Librarian librarian) {
        this.librarian = librarian;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("USER"));
    }

    @Override
    public String getPassword() {
        return librarian.getPassword();
    }

    @Override
    public String getUsername() {
        return librarian.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return librarian.isActive();
    }

    @Override
    public boolean isAccountNonLocked() {
        return librarian.isActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return librarian.isActive();
    }

    @Override
    public boolean isEnabled() {
        return librarian.isActive();
    }
}
