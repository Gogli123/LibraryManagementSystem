package com.gogli.librarymanagementsystem.services;

import com.gogli.librarymanagementsystem.Dtos.RegisterDto;
import com.gogli.librarymanagementsystem.exceptions.ResourceNotFoundException;
import com.gogli.librarymanagementsystem.exceptions.UsernameAlreadyExistsException;
import com.gogli.librarymanagementsystem.mapper.LibrarianMapper;
import com.gogli.librarymanagementsystem.models.Librarian;
import com.gogli.librarymanagementsystem.repo.LibrarianRepo;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibrarianService {

    private final LibrarianRepo repo;
    private final LibrarianMapper mapper;

    public LibrarianService(LibrarianRepo repo, LibrarianMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

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
        String oldUsername = librarian.getUsername();

        if (updatedLibrarian.getFirstName() != null && !updatedLibrarian.getFirstName().trim().isEmpty()) {
            librarian.setFirstName(updatedLibrarian.getFirstName());
        }
        if (updatedLibrarian.getLastName() != null && !updatedLibrarian.getLastName().trim().isEmpty()) {
            librarian.setLastName(updatedLibrarian.getLastName());
        }
        if (updatedLibrarian.getUsername() != null && !updatedLibrarian.getUsername().trim().isEmpty()) {
            if (oldUsername.equals(updatedLibrarian.getUsername())) {
                throw new IllegalArgumentException("Can not change to the same Username");
            } else if (repo.findByUsername(updatedLibrarian.getUsername()) != null) {
                throw new UsernameAlreadyExistsException("Username already taken");
            }

            librarian.setUsername(updatedLibrarian.getUsername());
        }
        if (updatedLibrarian.getEmail() != null && !updatedLibrarian.getEmail().trim().isEmpty()) {
            librarian.setEmail(updatedLibrarian.getEmail());
        }

        repo.save(librarian);
    }

    public void updateCustomerPassword(long librarianId, String existingPassword, String newPassword) {
        Librarian user = findLibrarianById(librarianId);

        if (!encoder.matches(existingPassword, user.getPassword())) {
            throw new IllegalArgumentException("Invalid existing password");
        }

        user.setPassword(encoder.encode(newPassword));
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

        allUsers.sort((librarian1, librarian2) -> (int) (librarian1.getLibrarianId() - librarian2.getLibrarianId()));
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
