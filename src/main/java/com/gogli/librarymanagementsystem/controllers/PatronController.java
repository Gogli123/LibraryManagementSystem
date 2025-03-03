package com.gogli.librarymanagementsystem.controllers;

import com.gogli.librarymanagementsystem.models.MembershipStatus;
import com.gogli.librarymanagementsystem.models.Patron;
import com.gogli.librarymanagementsystem.services.PatronService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patrons")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class PatronController {

    private final PatronService patronService;

    @GetMapping
    public List<Patron> getAllPatrons() {
        return patronService.getAllPatrons();
    }

    @GetMapping("/{patronId}")
    public ResponseEntity<Patron> getPatronById(@PathVariable long patronId) {
        Patron patron = patronService.getPatronById(patronId);
        return ResponseEntity.ok(patron);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> addPatron(@RequestBody @Valid Patron patron) {
        try {
            Patron newPatron = patronService.addPatron(patron);
            return ResponseEntity.status(HttpStatus.CREATED).body(newPatron);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{patronId}")
    public ResponseEntity<?> updatePatron(@PathVariable long patronId, @RequestBody Patron patron) {
        try {
            patronService.updatePatron(patronId, patron);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/suspend/{patronId}")
    public ResponseEntity<?> suspendPatron(@PathVariable long patronId) {
        try {
            patronService.setPatronSuspended(patronId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/expire/{patronId}")
    public ResponseEntity<?> expirePatron(@PathVariable long patronId) {
        try {
            patronService.setPatronExpired(patronId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/reactivate/{patronId}")
    public ResponseEntity<?> reactivatePatron(@PathVariable long patronId) {
        try {
            patronService.reactivatePatron(patronId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/validate/{patronId}")
    public ResponseEntity<MembershipStatus> validatePatronStatus(@PathVariable long patronId) {
        MembershipStatus status = patronService.validatePatronStatus(patronId);
        return ResponseEntity.ok(status);
    }
}