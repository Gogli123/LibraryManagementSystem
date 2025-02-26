package com.gogli.librarymanagementsystem.services;

import com.gogli.librarymanagementsystem.models.MembershipStatus;
import com.gogli.librarymanagementsystem.models.Patron;
import com.gogli.librarymanagementsystem.repo.PatronRepo;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class PatronSearchService {

    public final PatronRepo repo;

    public PatronSearchService(PatronRepo repo) {
        this.repo = repo;
    }

    //search methods
    public List<Patron> searchByName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return repo.findAllPatronByFirstName(firstName.trim());
    }

    public List<Patron> searchByLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return repo.findAllPatronByLastName(lastName.trim());
    }
    
    public List<Patron> searchByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return repo.findAllByEmailAddress(email.trim());
    }

    public Patron searchByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return new Patron();
        }
        return repo.findByPhoneNumber(phoneNumber.trim());
    }

    //filter methods
    public List<Patron> filterByMembershipStatus(MembershipStatus status) {
        if (status == null) {
            return Collections.emptyList();
        }
        return repo.findAllPatronByMembershipStatus(status);
    }

    public List<Patron> filterByActiveStatus(boolean isActive) {
        return repo.findActivePatrons(isActive);
    }
}
