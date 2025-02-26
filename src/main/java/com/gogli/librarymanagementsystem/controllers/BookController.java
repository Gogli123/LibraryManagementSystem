package com.gogli.librarymanagementsystem.controllers;

import com.gogli.librarymanagementsystem.models.Book;
import com.gogli.librarymanagementsystem.repo.BookRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BookController {
    
    @Autowired
    private BookRepo repo;

    @GetMapping("/books")
    public List<Book> getBooks(){
        return repo.findAll();
    }
}
