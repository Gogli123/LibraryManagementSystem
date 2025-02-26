package com.gogli.librarymanagementsystem.controllers;

import com.gogli.librarymanagementsystem.services.LibrarianService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LibrarianController {

    private final LibrarianService librarianService;

    public LibrarianController(LibrarianService librarianService) {
        this.librarianService = librarianService;
    }
    

}
