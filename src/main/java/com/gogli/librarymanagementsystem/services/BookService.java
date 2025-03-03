package com.gogli.librarymanagementsystem.services;

import com.gogli.librarymanagementsystem.exceptions.ResourceNotFoundException;
import com.gogli.librarymanagementsystem.models.Book;
import com.gogli.librarymanagementsystem.repo.BookRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class BookService {

    public final BookRepo repo;
    
    public List<Book> getAllBooks() {
        return repo.findAll();
    }

    public Book getBookById(long bookId) {
        return repo.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book could not be found"));
    }

    public void addBook(Book book) {
        if (book.getQuantity() >= 0) {
            book.setAvailable(book.getQuantity() > 0);
            book.setGenre(book.getGenre().toUpperCase());
            repo.save(book);
        } else throw new IllegalArgumentException("Book quantity can not be negative");
    }

    public void addBooks(List<Book> books) {
        if (books.stream().allMatch(book -> book.getQuantity() >= 0)) {
            books.forEach(book -> book.setAvailable(book.getQuantity() != null && book.getQuantity() > 0));
            repo.saveAll(books);
        } else {
            throw new IllegalArgumentException("One or more books have a negative quantity");
        }
    }

    @Transactional
    public void updateBook(long bookId, Book updatedBook) {
        Book book = getBookById(bookId);
        
        updateIfPresent(updatedBook.getTitle(), book::setTitle);
        updateIfPresent(updatedBook.getAuthor(), book::setAuthor);
        updateIfPresent(updatedBook.getGenre(), book::setGenre);

        repo.save(book);
    }

    private <T> void updateIfPresent(T value, Consumer<T> setter) {
        Optional.ofNullable(value).ifPresent(setter);
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
