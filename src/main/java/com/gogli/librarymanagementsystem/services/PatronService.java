package com.gogli.librarymanagementsystem.services;

import com.gogli.librarymanagementsystem.exceptions.ResourceNotFoundException;
import com.gogli.librarymanagementsystem.models.MembershipStatus;
import com.gogli.librarymanagementsystem.models.Patron;
import com.gogli.librarymanagementsystem.repo.PatronRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class PatronService {

    private final PatronRepo repo;
    
    public List<Patron> getAllPatrons() {
        return repo.findAll();
    }

    public Patron getPatronById(long patronId) {
        return repo.findById(patronId)
                .orElseThrow(() -> new ResourceNotFoundException("Patron could not be found"));
    }

    public Patron addPatron(Patron patron) {
        if (repo.findByEmail(patron.getEmailAddress()) != null) {
            throw new IllegalArgumentException("Email is already in use");
        }
        if (repo.findByPhoneNumber(patron.getPhoneNumber()) != null) {
            throw new IllegalArgumentException("Phone number is already in use");
        }

        repo.save(patron);
        return patron;
    }

    @Transactional
    public void updatePatron(long patronId, Patron updatedPatron) {
        Patron existingPatron = getPatronById(patronId);
        
        updateIfPresent(updatedPatron.getFirstName(), existingPatron::setFirstName);
        updateIfPresent(updatedPatron.getLastName(), existingPatron::setLastName);
        updateIfPresent(updatedPatron.getEmailAddress(), existingPatron::setEmailAddress);
        updateIfPresent(updatedPatron.getPhoneNumber(), existingPatron::setPhoneNumber);
        updateIfPresent(updatedPatron.getMembershipStatus(), existingPatron::setMembershipStatus);

        repo.save(existingPatron);
    }

    private <T> void updateIfPresent(T value, Consumer<T> setter) {
        Optional.ofNullable(value).ifPresent(setter);
    }

    public void setPatronSuspended(long patronId) {
        Patron patron = getPatronById(patronId);
        patron.setMembershipStatus(MembershipStatus.SUSPENDED);
        repo.save(patron);
    }

    public void setPatronExpired(long patronId) {
        Patron patron = getPatronById(patronId);
        patron.setMembershipStatus(MembershipStatus.EXPIRED);
        repo.save(patron);
    }

    public void reactivatePatron(long patronId) {
        Patron patron = getPatronById(patronId);
        if (patron.getMembershipStatus() == MembershipStatus.ACTIVE) {
            throw new IllegalArgumentException("Patron is already active");
        }
        patron.setMembershipStatus(MembershipStatus.ACTIVE);
        repo.save(patron);
    }

    public MembershipStatus validatePatronStatus(long patronId) {
        Patron patron = getPatronById(patronId);

        if (patron.getMembershipStatus() == MembershipStatus.ACTIVE) {
            return MembershipStatus.ACTIVE;
        }
        if (patron.getMembershipStatus() == MembershipStatus.SUSPENDED) {
            return MembershipStatus.SUSPENDED;
        } else {
            return MembershipStatus.EXPIRED;
        }
    }
}
