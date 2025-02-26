package com.gogli.librarymanagementsystem.services;

import com.gogli.librarymanagementsystem.exceptions.ResourceNotFoundException;
import com.gogli.librarymanagementsystem.models.MembershipStatus;
import com.gogli.librarymanagementsystem.models.Patron;
import com.gogli.librarymanagementsystem.repo.PatronRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatronService {

    private final PatronRepo repo;

    public PatronService(PatronRepo repo) {
        this.repo = repo;
    }

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
    
    public void updatePatron(long patronId, Patron updatedPatron){
        Patron patron = getPatronById(patronId);
        
        patron.setFirstName(updatedPatron.getFirstName());
        patron.setLastName(updatedPatron.getLastName());
        patron.setEmailAddress(updatedPatron.getEmailAddress());
        patron.setPhoneNumber(updatedPatron.getPhoneNumber());
        patron.setMembershipStatus(updatedPatron.getMembershipStatus());
        
        repo.save(patron);
    }
    
    public void setPatronInactive(long patronId){
        Patron patron = getPatronById(patronId);
        patron.setActive(false);
        repo.save(patron);
    }

    public void reactivatePatron(long patronId) {
        Patron patron = getPatronById(patronId);
        if (patron.isActive()) {
            throw new IllegalArgumentException("Patron is already active");
        }
        patron.setActive(true);
        repo.save(patron);
    }

    public void validatePatronStatus(long patronId) {
        Patron patron = getPatronById(patronId);
        
        if (!patron.isActive()) {
            throw new IllegalStateException("Patron is not active");
        }
        if (patron.getMembershipStatus() == MembershipStatus.SUSPENDED) {
            throw new IllegalStateException("Patron membership is suspended");
        }
        if (patron.getMembershipStatus() == MembershipStatus.EXPIRED) {
            throw new IllegalStateException("Patron membership is expired");
        }
    }
}
