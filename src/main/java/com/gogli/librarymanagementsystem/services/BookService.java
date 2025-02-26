package com.gogli.librarymanagementsystem.services;

import com.gogli.librarymanagementsystem.exceptions.ResourceNotFoundException;
import com.gogli.librarymanagementsystem.models.Book;
import com.gogli.librarymanagementsystem.repo.BookRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    public final BookRepo repo;

    public BookService(BookRepo repo) {
        this.repo = repo;
    }

    public List<Book> getAllBooks() {
        return repo.findAll();
    }

    public Book getBookById(long bookId) {
        return repo.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book could not be found"));
    }

    public void addBook(Book book) {
        repo.save(book);
    }

    public void addBooks(List<Book> books) {
        repo.saveAll(books);
    }

    public void updateBook(long bookId, Book updatedBook) {
        Book book = getBookById(bookId);

        if (updatedBook.getTitle() != null) {
            book.setTitle(updatedBook.getTitle());
        }
        if (updatedBook.getAuthor() != null) {
            book.setAuthor(updatedBook.getAuthor());
        }
        if (updatedBook.getGenre() != null) {
            book.setGenre(updatedBook.getGenre());
        }
        if (updatedBook.getIsbn() != null) {
            book.setIsbn(updatedBook.getIsbn());
        }

        repo.save(book);
    }

    public void updateBookQuantity(long bookId, Long newQuantity) {
        Book book = getBookById(bookId);
        
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Insufficient book quantity");
        }
        
        book.setQuantity(newQuantity);
        book.setAvailable(newQuantity > 0);
        repo.save(book);
    }
    
    public void deleteBook(long bookId) {
        Book book = getBookById(bookId);
        book.setAvailable(false);
        repo.save(book);
    }
}
