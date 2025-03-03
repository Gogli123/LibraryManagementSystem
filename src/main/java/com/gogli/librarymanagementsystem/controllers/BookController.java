package com.gogli.librarymanagementsystem.controllers;

import com.gogli.librarymanagementsystem.models.Book;
import com.gogli.librarymanagementsystem.services.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class BookController {
    
    private final BookService bookService;

    @GetMapping("/books")
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/books/{bookId}")
    public Book getBookById(@PathVariable long bookId) {
        return bookService.getBookById(bookId);
    }

    @PostMapping("/books")
    @ResponseStatus(HttpStatus.CREATED)
    public void addBook(@RequestBody @Valid Book book) {
        bookService.addBook(book);
    }

    @PostMapping("/books/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    public void addBooks(@RequestBody @Valid List<Book> books) {
        bookService.addBooks(books);
    }

    @PatchMapping("/books/{bookId}")
    public void updateBook(@PathVariable long bookId, @RequestBody Book book) {
        bookService.updateBook(bookId, book);
    }

    @PatchMapping("/books/quantity/{bookId}")
    public void updateBookQuantity(@PathVariable long bookId, @RequestBody Long quantity) {
        bookService.updateBookQuantity(bookId, quantity);
    }

    @DeleteMapping("/books/{bookId}")
    public void deleteBook(@PathVariable long bookId) {
        bookService.deleteBook(bookId);
    }
}
