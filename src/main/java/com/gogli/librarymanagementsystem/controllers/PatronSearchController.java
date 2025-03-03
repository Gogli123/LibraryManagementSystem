package com.gogli.librarymanagementsystem.controllers;

import com.gogli.librarymanagementsystem.models.MembershipStatus;
import com.gogli.librarymanagementsystem.models.Patron;
import com.gogli.librarymanagementsystem.services.PatronSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patrons/search")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class PatronSearchController {

    private final PatronSearchService searchService;

    @GetMapping("/name/{firstName}")
    public ResponseEntity<List<Patron>> searchByName(@PathVariable String firstName) {
        return ResponseEntity.ok(searchService.searchByName(firstName));
    }

    @GetMapping("/lastname/{lastName}")
    public ResponseEntity<List<Patron>> searchByLastName(@PathVariable String lastName) {
        return ResponseEntity.ok(searchService.searchByLastName(lastName));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<List<Patron>> searchByEmail(@PathVariable String email) {
        return ResponseEntity.ok(searchService.searchByEmail(email));
    }

    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<Patron> searchByPhone(@PathVariable String phoneNumber) {
        return ResponseEntity.ok(searchService.searchByPhoneNumber(phoneNumber));
    }

    @GetMapping("/filter/status/{status}")
    public ResponseEntity<List<Patron>> filterByStatus(@PathVariable String status) {
        return ResponseEntity.ok(searchService.filterByMembershipStatus(MembershipStatus.valueOf(status.toUpperCase())));
    }

    @GetMapping("/filter/active")
    public ResponseEntity<List<Patron>> filterActive() {
        return ResponseEntity.ok(searchService.filterByActiveStatus());
    }
}
