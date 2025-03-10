package com.gogli.librarymanagementsystem;

import com.gogli.librarymanagementsystem.Dtos.RegisterDto;
import com.gogli.librarymanagementsystem.Dtos.UpdatePasswordRequestDto;
import com.gogli.librarymanagementsystem.exceptions.UsernameAlreadyExistsException;
import com.gogli.librarymanagementsystem.mapper.LibrarianMapper;
import com.gogli.librarymanagementsystem.models.Librarian;
import com.gogli.librarymanagementsystem.repo.LibrarianRepo;
import com.gogli.librarymanagementsystem.services.LibrarianService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LibrarianServiceTest {

    @Mock
    private LibrarianRepo librarianRepo;

    @Mock
    private LibrarianMapper librarianMapper;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

    private RegisterDto registerDto;

    @InjectMocks
    private LibrarianService librarianService;

    @BeforeEach
    void setUp() {
        registerDto = RegisterDto.builder()
                .firstName("unittest")
                .lastName("unittest")
                .username("unittest")
                .email("unittest@gmail.com")
                .password("unittest1")
                .confirmPassword("unittest1")
                .build();
    }

    @Test
    void registerLibrarian_Successfully() {
        Librarian mockLibrarian = Librarian.builder()
                .firstName(registerDto.getFirstName())
                .lastName(registerDto.getLastName())
                .username(registerDto.getUsername())
                .email(registerDto.getEmail())
                .password(encoder.encode(registerDto.getPassword()))
                .build();

        when(librarianRepo.findByEmail(registerDto.getEmail())).thenReturn(null);
        when(librarianRepo.findByUsername(registerDto.getUsername())).thenReturn(null);
        when(librarianMapper.toEntity(registerDto)).thenReturn(mockLibrarian);

        librarianService.register(registerDto);

        verify(librarianRepo).save(mockLibrarian);
        verify(librarianMapper).toEntity(registerDto);
        assertEquals(mockLibrarian.getUsername(), registerDto.getUsername());
    }

    @Test
    void registerLibrarian_ShouldThrowException_WhenEmailIsTaken() {
        Librarian existingLibrarian = Librarian.builder()
                .firstName(registerDto.getFirstName())
                .lastName(registerDto.getLastName())
                .username(registerDto.getUsername())
                .email(registerDto.getEmail())
                .password(encoder.encode(registerDto.getPassword()))
                .build();

        when(librarianRepo.findByEmail(registerDto.getEmail())).thenReturn(existingLibrarian);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> librarianService.register(registerDto)
        );

        assertEquals("Email is already in use", exception.getMessage());

        verify(librarianRepo, never()).save(existingLibrarian);
    }

    @Test
    void registerLibrarian_ShouldThrowException_WhenUsernameIsTaken() {
        Librarian existingLibrarian = Librarian.builder()
                .firstName(registerDto.getFirstName())
                .lastName(registerDto.getLastName())
                .username(registerDto.getUsername())
                .email(registerDto.getEmail())
                .password(encoder.encode(registerDto.getPassword()))
                .build();

        when(librarianRepo.findByUsername(registerDto.getUsername())).thenReturn(existingLibrarian);

        UsernameAlreadyExistsException exception = assertThrows(
                UsernameAlreadyExistsException.class,
                () -> librarianService.register(registerDto)
        );

        assertEquals("Username is already taken", exception.getMessage());

        verify(librarianRepo, never()).save(existingLibrarian);
    }

    @Test
    void registerLibrarian_ShouldThrowException_WhenPasswordsDoNotMatch() {
        registerDto.setPassword("invalid");

        when(librarianRepo.findByEmail(registerDto.getEmail())).thenReturn(null);
        when(librarianRepo.findByUsername(registerDto.getUsername())).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> librarianService.register(registerDto)
        );

        assertEquals("Passwords do not match", exception.getMessage());
        verify(librarianRepo, never()).save(any());
    }

    @Test
    void updateLibrarianInformation_Successfully() {
        Librarian mockLibrarian = Librarian.builder()
                .firstName(registerDto.getFirstName())
                .lastName(registerDto.getLastName())
                .username(registerDto.getUsername())
                .email(registerDto.getEmail())
                .password(encoder.encode(registerDto.getPassword()))
                .build();

        Librarian updatedLibrarian = Librarian.builder()
                .firstName("test")
                .lastName("test")
                .username("test")
                .email("test@gmail.com")
                .build();

        when(librarianRepo.findById(mockLibrarian.getLibrarianId())).thenReturn(Optional.of(mockLibrarian));

        librarianService.updateLibrarian(mockLibrarian.getLibrarianId(), updatedLibrarian);

        verify(librarianRepo).save(mockLibrarian);
        assertEquals(updatedLibrarian.getFirstName(), mockLibrarian.getFirstName());
        assertEquals(updatedLibrarian.getLastName(), mockLibrarian.getLastName());
        assertEquals(updatedLibrarian.getUsername(), mockLibrarian.getUsername());
        assertEquals(updatedLibrarian.getEmail(), mockLibrarian.getEmail());
    }

    @Test
    void updateLibrarianPassword_Successfully() {
        Librarian mockLibrarian = Librarian.builder()
                .firstName(registerDto.getFirstName())
                .lastName(registerDto.getLastName())
                .username(registerDto.getUsername())
                .email(registerDto.getEmail())
                .password(encoder.encode(registerDto.getPassword()))
                .build();

        UpdatePasswordRequestDto updatePasswordRequestDto = UpdatePasswordRequestDto.builder()
                .oldPassword(registerDto.getPassword())
                .newPassword("newPassword")
                .build();

        when(librarianRepo.findById(mockLibrarian.getLibrarianId())).thenReturn(Optional.of(mockLibrarian));

        librarianService.updateLibrarianPassword(mockLibrarian.getLibrarianId(), updatePasswordRequestDto);

        verify(librarianRepo).save(mockLibrarian);
        assertTrue(encoder.matches(updatePasswordRequestDto.getNewPassword(), mockLibrarian.getPassword()));
    }

    @Test
    void updateLibrarianPassword_ShouldThrowException_WhenOldPasswordIsInvalid() {
        Librarian mockLibrarian = Librarian.builder()
                .firstName(registerDto.getFirstName())
                .lastName(registerDto.getLastName())
                .username(registerDto.getUsername())
                .email(registerDto.getEmail())
                .password(encoder.encode(registerDto.getPassword()))
                .build();

        UpdatePasswordRequestDto updatePasswordRequestDto = UpdatePasswordRequestDto.builder()
                .oldPassword("invalid")
                .newPassword("newPassword")
                .build();

        when(librarianRepo.findById(mockLibrarian.getLibrarianId())).thenReturn(Optional.of(mockLibrarian));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> librarianService.updateLibrarianPassword(mockLibrarian.getLibrarianId(), updatePasswordRequestDto)
        );

        assertEquals("Invalid existing password", exception.getMessage());
        verify(librarianRepo, never()).save(any());
    }

    @Test
    void setLibrarianToInactive_Successfully() {
        Librarian mockLibrarian = Librarian.builder()
                .librarianId(1)
                .firstName(registerDto.getFirstName())
                .lastName(registerDto.getLastName())
                .username(registerDto.getUsername())
                .email(registerDto.getEmail())
                .password(encoder.encode(registerDto.getPassword()))
                .isActive(true)
                .build();

        when(librarianRepo.findById(mockLibrarian.getLibrarianId())).thenReturn(Optional.of(mockLibrarian));

        librarianService.setLibrarianToInactive(mockLibrarian.getLibrarianId());

        verify(librarianRepo).save(mockLibrarian);
        assertFalse(mockLibrarian.isActive());
    }

    @Test
    void setLibrarianToInactive_ShouldThrowException_WhenAlreadyInactive() {
        Librarian mockLibrarian = Librarian.builder()
                .librarianId(1)
                .firstName(registerDto.getFirstName())
                .lastName(registerDto.getLastName())
                .username(registerDto.getUsername())
                .email(registerDto.getEmail())
                .password(encoder.encode(registerDto.getPassword()))
                .isActive(false)
                .build();

        when(librarianRepo.findById(mockLibrarian.getLibrarianId())).thenReturn(Optional.of(mockLibrarian));
        
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> librarianService.setLibrarianToInactive(mockLibrarian.getLibrarianId())
        );
        
        assertEquals("User is already inactive", exception.getMessage());
        verify(librarianRepo, never()).save(any());
        assertFalse(mockLibrarian.isActive());
    }
}
