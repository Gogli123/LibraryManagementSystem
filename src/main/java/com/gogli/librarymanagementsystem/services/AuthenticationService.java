package com.gogli.librarymanagementsystem.services;

import com.gogli.librarymanagementsystem.Dtos.LoginDto;
import com.gogli.librarymanagementsystem.exceptions.AccountLockedException;
import com.gogli.librarymanagementsystem.models.AuthenticationResponse;
import com.gogli.librarymanagementsystem.models.Librarian;
import com.gogli.librarymanagementsystem.models.Token;
import com.gogli.librarymanagementsystem.repo.LibrarianRepo;
import com.gogli.librarymanagementsystem.repo.TokenRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final LibrarianRepo repo;
    private final AuthenticationManager authManager;
    private final JWTService jwtService;
    private final TokenRepo tokenRepo;

    public AuthenticationResponse authenticate(LoginDto loginDto) {
        Librarian librarian = repo.findByUsername(loginDto.getUsername());
        if (librarian == null) {
            throw new RuntimeException("User not found");
        }
        if (!librarian.isActive()) {
            throw new AccountLockedException("Account locked");
        }

        Authentication authentication =
                authManager.authenticate(new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtService.generateToken(loginDto.getUsername());

        invalidateTokens(librarian);
        saveUserToken(token, librarian);
        
        return new AuthenticationResponse(token);
    }

    public void invalidateTokens(Librarian librarian) {
        List<Token> validTokensByUser = tokenRepo.findAllValidTokensByLibrarian(librarian.getLibrarianId());
        if (!validTokensByUser.isEmpty()) {
            validTokensByUser.forEach(t -> {
                t.setExpired(true);
                t.setRevoked(true);
                tokenRepo.save(t);
            });
        }

        tokenRepo.saveAll(validTokensByUser);
    }

    public void saveUserToken(String jwtToken, Librarian librarian) {
        Token token = new Token();
        token.setToken(jwtToken);
        token.setLibrarian(librarian);
        token.setExpired(false);
        token.setRevoked(false);

        tokenRepo.save(token);
    }
}
