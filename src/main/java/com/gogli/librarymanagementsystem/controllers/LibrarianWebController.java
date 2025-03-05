package com.gogli.librarymanagementsystem.controllers;

import com.gogli.librarymanagementsystem.models.Librarian;
import com.gogli.librarymanagementsystem.services.LibrarianService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/librarians")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class LibrarianWebController {
    
    private final LibrarianService librarianService;

    @GetMapping
    public String listLibrarians(Model model) {
        model.addAttribute("librarians", librarianService.getAllLibrarians());
        return "librarians/list";
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        model.addAttribute("librarian", librarianService.findLibrarianById(id));
        return "librarians/update";
    }

    @PostMapping("/update/{id}")
    public String updateLibrarian(@PathVariable Long id, @ModelAttribute Librarian librarian) {
        librarianService.updateLibrarian(id, librarian);
        return "redirect:/librarians";
    }

    @GetMapping("/deactivate/{id}")
    public String deactivateLibrarian(@PathVariable Long id) {
        librarianService.setLibrarianToInactive(id);
        return "redirect:/librarians";
    }
}
