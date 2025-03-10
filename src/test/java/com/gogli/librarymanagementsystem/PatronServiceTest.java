package com.gogli.librarymanagementsystem;

import com.gogli.librarymanagementsystem.exceptions.ResourceNotFoundException;
import com.gogli.librarymanagementsystem.models.MembershipStatus;
import com.gogli.librarymanagementsystem.models.Patron;
import com.gogli.librarymanagementsystem.repo.PatronRepo;
import com.gogli.librarymanagementsystem.services.PatronService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatronServiceTest {

    @Mock
    private PatronRepo patronRepo;

    @InjectMocks
    private PatronService patronService;

    private Patron mockPatron;

    @BeforeEach
    void setUp() {
        mockPatron = Patron.builder()
                .patronId(1)
                .firstName("patron")
                .lastName("patron")
                .emailAddress("patron@gmail.com")
                .phoneNumber("1234567890")
                .membershipStatus(MembershipStatus.ACTIVE)
                .build();
    }

    @Test
    void add_patronSuccessfully() {
        when(patronRepo.findByEmail(mockPatron.getEmailAddress())).thenReturn(null);
        when(patronRepo.findByPhoneNumber(mockPatron.getPhoneNumber())).thenReturn(null);

        patronService.addPatron(mockPatron);

        verify(patronRepo).save(mockPatron);
    }

    @Test
    void addPatron_ShouldThrowException_WhenEmailIsTaken() {
        when(patronRepo.findByEmail(mockPatron.getEmailAddress())).thenReturn(mockPatron);

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> patronService.addPatron(mockPatron));

        assertEquals("Email is already in use", illegalArgumentException.getMessage());
        verify(patronRepo, never()).save(mockPatron);
    }

    @Test
    void addPatron_ShouldThrowException_WhenPhoneNumberIsTaken() {
        when(patronRepo.findByEmail(mockPatron.getEmailAddress())).thenReturn(null);
        when(patronRepo.findByPhoneNumber(mockPatron.getPhoneNumber())).thenReturn(mockPatron);

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> patronService.addPatron(mockPatron));

        assertEquals("Phone number is already in use", illegalArgumentException.getMessage());
        verify(patronRepo, never()).save(mockPatron);
    }

    @Test
    void updatePatronSuccessfully() {
        Patron updatedPatron = Patron.builder()
                .patronId(1)
                .firstName("updatedPatron")
                .lastName("updatedPatron")
                .emailAddress("updatedPatron@gmail.com")
                .phoneNumber("123456789")
                .membershipStatus(MembershipStatus.ACTIVE)
                .build();

        when(patronRepo.findById(mockPatron.getPatronId())).thenReturn(Optional.of(mockPatron));

        patronService.updatePatron(mockPatron.getPatronId(), updatedPatron);

        assertEquals(updatedPatron.getEmailAddress(), mockPatron.getEmailAddress());
        assertEquals(updatedPatron.getFirstName(), mockPatron.getFirstName());
        assertEquals(updatedPatron.getLastName(), mockPatron.getLastName());
        assertEquals(updatedPatron.getPhoneNumber(), mockPatron.getPhoneNumber());
        assertEquals(MembershipStatus.ACTIVE, mockPatron.getMembershipStatus());
        verify(patronRepo).save(mockPatron);
    }

    @Test
    void updatePatron_ShouldThrowException_WhenPatronNotFound() {
        Patron updatedPatron = Patron.builder()
                .patronId(1)
                .firstName("updatedPatron")
                .lastName("updatedPatron")
                .emailAddress("updatedPatron@gmail.com")
                .phoneNumber("123456789")
                .membershipStatus(MembershipStatus.ACTIVE)
                .build();

        ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class,
                () -> patronService.updatePatron(mockPatron.getPatronId(), updatedPatron));

        assertEquals("Patron could not be found", resourceNotFoundException.getMessage());
        verify(patronRepo, never()).save(mockPatron);
    }

    @Test
    void setPatronSuspendedSuccessfully() {
        when(patronRepo.findById(mockPatron.getPatronId())).thenReturn(Optional.of(mockPatron));

        patronService.setPatronSuspended(mockPatron.getPatronId());

        assertEquals(MembershipStatus.SUSPENDED, mockPatron.getMembershipStatus());
        verify(patronRepo).save(mockPatron);
    }

    @Test
    void setPatronSuspended_ShouldThrowException_WhenPatronNotFound() {
        ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class,
                () -> patronService.setPatronSuspended(mockPatron.getPatronId()));

        assertEquals("Patron could not be found", resourceNotFoundException.getMessage());
        verify(patronRepo, never()).save(mockPatron);
    }

    @Test
    void setPatronExpiredSuccessfully() {
        when(patronRepo.findById(mockPatron.getPatronId())).thenReturn(Optional.of(mockPatron));

        patronService.setPatronExpired(mockPatron.getPatronId());

        assertEquals(MembershipStatus.EXPIRED, mockPatron.getMembershipStatus());
        verify(patronRepo).save(mockPatron);
    }

    @Test
    void setPatronExpired_ShouldThrowException_WhenPatronNotFound() {
        ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class,
                () -> patronService.setPatronExpired(mockPatron.getPatronId()));

        assertEquals("Patron could not be found", resourceNotFoundException.getMessage());
        verify(patronRepo, never()).save(mockPatron);
    }

    @Test
    void reactivatePatronSuccessfully() {
        mockPatron.setMembershipStatus(MembershipStatus.SUSPENDED);

        when(patronRepo.findById(mockPatron.getPatronId())).thenReturn(Optional.of(mockPatron));

        patronService.reactivatePatron(mockPatron.getPatronId());

        assertEquals(MembershipStatus.ACTIVE, mockPatron.getMembershipStatus());
        verify(patronRepo).save(mockPatron);
    }

    @Test
    void reactivatePatron_ShouldThrowException_WhenPatronNotFound() {
        ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class,
                () -> patronService.reactivatePatron(mockPatron.getPatronId()));

        assertEquals("Patron could not be found", resourceNotFoundException.getMessage());
        verify(patronRepo, never()).save(mockPatron);
    }
}
