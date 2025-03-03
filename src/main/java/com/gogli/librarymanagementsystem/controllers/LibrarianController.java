package com.gogli.librarymanagementsystem.controllers;

import com.gogli.librarymanagementsystem.Dtos.UpdatePasswordRequestDto;
import com.gogli.librarymanagementsystem.models.Librarian;
import com.gogli.librarymanagementsystem.services.LibrarianService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/librarians")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class LibrarianController {

    private final LibrarianService librarianService;
    
    @GetMapping
    public List<Librarian> getAllLibrarians() {
        return librarianService.getAllLibrarians();
    }

    @GetMapping("/{librarianId}")
    public Librarian getLibrarianById(@PathVariable Long librarianId) {
        return librarianService.findLibrarianById(librarianId);
    }

    @PatchMapping("/{librarianId}")
    public void updateLibrarian(@PathVariable long librarianId, @RequestBody Librarian librarian) {
        librarianService.updateLibrarian(librarianId, librarian);
    }

    @PatchMapping("/password/{librarianId}")
    public void updatePassword(@PathVariable long librarianId,
                               @RequestBody UpdatePasswordRequestDto updatePasswordRequestDto) {
        librarianService.updateLibrarianPassword(librarianId, updatePasswordRequestDto);
    }

    @DeleteMapping("/deactivate/{librarianId}")
    public void deactivateLibrarian(@PathVariable Long librarianId) {
        librarianService.setLibrarianToInactive(librarianId);
    }

    @GetMapping("/sort/desc")
    public List<Librarian> getLibrariansSortedByIdDesc() {
        return librarianService.sortUsersByIdDesc();
    }

    @GetMapping("/sort/asc")
    public List<Librarian> getLibrariansSortedByIdAsc() {
        return librarianService.sortUsersByIdAsc();
    }
}
