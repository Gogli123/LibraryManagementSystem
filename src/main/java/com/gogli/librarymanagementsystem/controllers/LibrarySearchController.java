package com.gogli.librarymanagementsystem.controllers;

import com.gogli.librarymanagementsystem.models.Book;
import com.gogli.librarymanagementsystem.services.LibrarySearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/books/search")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class LibrarySearchController {
    
    private final LibrarySearchService searchService;

    @GetMapping("/title/{title}")
    public ResponseEntity<List<Book>> searchByTitle(@PathVariable String title) {
        return ResponseEntity.ok(searchService.findBookByTitle(title));
    }

    @GetMapping("/author/{author}")
    public ResponseEntity<List<Book>> searchByAuthor(@PathVariable String author) {
        return ResponseEntity.ok(searchService.findBookByAuthor(author));
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<List<Book>> searchByIsbn(@PathVariable String isbn) {
        return ResponseEntity.ok(searchService.findBookByIsbn(isbn));
    }

    @GetMapping("/filter/genre/{genre}")
    public ResponseEntity<List<Book>> filterByGenre(@PathVariable String genre) {
        return ResponseEntity.ok(searchService.filterBookByGenre(genre.toUpperCase()));
    }

    @GetMapping("/filter/available/{available}")
    public ResponseEntity<List<Book>> filterByAvailability(@PathVariable boolean available) {
        return ResponseEntity.ok(searchService.filterByAvailability(available));
    }
}
