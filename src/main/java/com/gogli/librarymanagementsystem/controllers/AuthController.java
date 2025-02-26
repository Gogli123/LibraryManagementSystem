package com.gogli.librarymanagementsystem.controllers;

import com.gogli.librarymanagementsystem.Dtos.LoginDto;
import com.gogli.librarymanagementsystem.Dtos.RegisterDto;
import com.gogli.librarymanagementsystem.exceptions.AccountLockedException;
import com.gogli.librarymanagementsystem.services.AuthenticationService;
import com.gogli.librarymanagementsystem.services.LibrarianService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthenticationService authenticationService;
    private final LibrarianService librarianService;
    
    public AuthController(AuthenticationService authenticationService, LibrarianService librarianService) {
        this.authenticationService = authenticationService;
        this.librarianService = librarianService;
    }
    
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginDto loginDto) {
        try {
            String token = authenticationService.authenticate(loginDto);
            System.out.println(token);
            return ResponseEntity.ok().body(token);
        } catch (AuthenticationException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials");
        } catch (AccountLockedException e) {
            return ResponseEntity.status(HttpStatus.LOCKED).body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterDto registerDto) {
        try {
            librarianService.register(registerDto);
            return ResponseEntity.ok().body("Successfully registered");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
