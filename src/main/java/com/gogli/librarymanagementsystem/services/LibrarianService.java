package com.gogli.librarymanagementsystem.services;

import com.gogli.librarymanagementsystem.Dtos.RegisterDto;
import com.gogli.librarymanagementsystem.Dtos.UpdatePasswordRequestDto;
import com.gogli.librarymanagementsystem.exceptions.ResourceNotFoundException;
import com.gogli.librarymanagementsystem.exceptions.UsernameAlreadyExistsException;
import com.gogli.librarymanagementsystem.mapper.LibrarianMapper;
import com.gogli.librarymanagementsystem.models.Librarian;
import com.gogli.librarymanagementsystem.repo.LibrarianRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class LibrarianService {

    private final LibrarianRepo repo;
    private final LibrarianMapper mapper;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

    public void register(RegisterDto registerDto) {
        // Check if the email is already taken
        if (repo.findByEmail(registerDto.getEmail()) != null) {
            throw new IllegalArgumentException("Email is already in use");
        }

        // Check if the username is already taken
        if (repo.findByUsername(registerDto.getUsername()) != null) {
            throw new UsernameAlreadyExistsException("Username already taken");
        }

        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        registerDto.setPassword(encoder.encode(registerDto.getPassword()));
        var librarian = mapper.toEntity(registerDto);
        repo.save(librarian);
    }

    public List<Librarian> getAllLibrarians() {
        return repo.findAll();
    }

    public Librarian findLibrarianById(Long librarianId) {
        return repo.findById(librarianId)
                .orElseThrow(() -> new ResourceNotFoundException("Librarian not found"));
    }

    @Transactional
    public void updateLibrarian(long librarianId, Librarian updatedLibrarian) {
        Librarian librarian = findLibrarianById(librarianId);

        String newFirstName = updatedLibrarian.getFirstName() != null ? updatedLibrarian.getFirstName().trim() : null;
        String newLastName = updatedLibrarian.getLastName() != null ? updatedLibrarian.getLastName().trim() : null;
        String newUsername = updatedLibrarian.getUsername() != null ? updatedLibrarian.getUsername().trim() : null;
        String newEmail = updatedLibrarian.getEmail() != null ? updatedLibrarian.getEmail().trim() : null;

        updateIfPresent(newFirstName, librarian::setFirstName);
        updateIfPresent(newLastName, librarian::setLastName);

        if (newUsername != null && !newUsername.equals(librarian.getUsername())) {
            if (repo.findByUsername(newUsername) != null) {
                throw new UsernameAlreadyExistsException("Username already exists");
            }
            librarian.setUsername(newUsername);
        }

        if (newEmail != null && !newEmail.equals(librarian.getEmail())) {
            if (repo.findByEmail(newEmail) != null) {
                throw new UsernameAlreadyExistsException("Email already exists");
            }
            librarian.setEmail(newEmail);
        }

        repo.save(librarian);
    }

    private <T> void updateIfPresent(T value, Consumer<T> setter) {
        Optional.ofNullable(value).ifPresent(setter);
    }

    public void updateLibrarianPassword(long librarianId, UpdatePasswordRequestDto updatePasswordRequestDto) {
        Librarian user = findLibrarianById(librarianId);

        if (!encoder.matches(updatePasswordRequestDto.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid existing password");
        }

        user.setPassword(encoder.encode(updatePasswordRequestDto.getNewPassword()));
        repo.save(user);
    }

    public void setLibrarianToInactive(Long userId) {
        Librarian librarian = findLibrarianById(userId);
        if (!librarian.isActive()) {
            throw new IllegalArgumentException("User is already inactive");
        }

        librarian.setActive(false);
        repo.save(librarian);
    }

    public List<Librarian> sortUsersByIdDesc() {
        List<Librarian> allUsers = getAllLibrarians();
        if (allUsers == null) {
            throw new ResourceNotFoundException("No users found");
        }

        allUsers.sort((librarian1, librarian2) -> (int) (librarian2.getLibrarianId() - librarian1.getLibrarianId()));
        return allUsers;
    }

    public List<Librarian> sortUsersByIdAsc() {
        List<Librarian> allUsers = getAllLibrarians();
        if (allUsers == null) {
            throw new ResourceNotFoundException("No users found");
        }

        allUsers.sort((librarian1, librarian2) -> (int) (librarian1.getLibrarianId() - librarian2.getLibrarianId()));
        return allUsers;
    }
}
