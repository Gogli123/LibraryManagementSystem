package com.gogli.librarymanagementsystem.repo;

import com.gogli.librarymanagementsystem.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepo extends JpaRepository<Book, Long> {

    List<Book> findAllBookByTitle(String title);
    List<Book> findAllBookByAuthor(String author);
    List<Book> findAllBookByIsbn(String isbn);
    List<Book> filterAllBookByGenre(String genre);
    
    @Query("SELECT b FROM Book b WHERE b.isAvailable = :isAvailable")
    List<Book> filterAllByAvailability(boolean isAvailable);
}
