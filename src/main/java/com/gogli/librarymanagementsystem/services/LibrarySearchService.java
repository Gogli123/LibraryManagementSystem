package com.gogli.librarymanagementsystem.services;

import com.gogli.librarymanagementsystem.models.Book;
import com.gogli.librarymanagementsystem.repo.BookRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LibrarySearchService {

    private final BookRepo repo;
    
    //search methods
    public List<Book> findBookByTitle(String title){
        return repo.findAllBookByTitle(title);
    }
    
    public List<Book> findBookByAuthor(String author) {
        return repo.findAllBookByAuthor(author);
    }

    public List<Book> findBookByIsbn(String isbn){
        return repo.findAllBookByIsbn(isbn);
    }
    
    //filtering methods
    public List<Book> filterBookByGenre(String genre){
        return repo.filterAllBookByGenre(genre);
    }
    
    public List<Book> filterByAvailability(boolean available){
        return repo.filterAllByAvailability(available);
    }
}
