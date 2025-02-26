package com.gogli.librarymanagementsystem.services;

import com.gogli.librarymanagementsystem.Dtos.LoginDto;
import com.gogli.librarymanagementsystem.exceptions.AccountLockedException;
import com.gogli.librarymanagementsystem.repo.LibrarianRepo;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final LibrarianRepo repo;
    private final AuthenticationManager authManager;
    private final JWTService jwtService;

    public AuthenticationService(LibrarianRepo repo, JWTService jwtService, AuthenticationManager authenticationManager) {
        this.authManager = authenticationManager;
        this.repo = repo;
        this.jwtService = jwtService;
    }

    public String authenticate(LoginDto loginDto) {
        if (!repo.findByUsername(loginDto.getUsername()).isActive()) throw new AccountLockedException("Account locked");

        Authentication authentication =
                authManager.authenticate(new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(), loginDto.getPassword()));

        if (authentication.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return jwtService.generateToken(loginDto.getUsername());
        } else throw new RuntimeException();
    }
}
