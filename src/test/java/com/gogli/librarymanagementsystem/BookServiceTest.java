package com.gogli.librarymanagementsystem;
import com.gogli.librarymanagementsystem.exceptions.ResourceNotFoundException;
import com.gogli.librarymanagementsystem.models.Book;
import com.gogli.librarymanagementsystem.repo.BookRepo;
import com.gogli.librarymanagementsystem.services.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    
    @Mock
    private BookRepo bookRepo;
    
    @InjectMocks
    private BookService bookService;
    
    private Book mockBook;
    
    @BeforeEach
    void setUp() {
        mockBook = Book.builder()
                .bookId(1)
                .title("Book")
                .author("Author")
                .isbn("ISBN")
                .genre("Genre")
                .isAvailable(true)
                .build();
    }
    
    @Test
    void add_BookSuccessfully(){
        mockBook.setQuantity(2L);
        
        bookRepo.save(mockBook);
        
        verify(bookRepo).save(mockBook);
    }
    
    @Test
    void addBook_ShouldThrowException_WhenQuantityIsNegative(){
        mockBook.setQuantity(-1L);
        IllegalArgumentException IllegalArgumentException =
                assertThrows(IllegalArgumentException.class, () -> bookService.addBook(mockBook));
        
        assertEquals("Book quantity can not be negative", IllegalArgumentException.getMessage());
        verify(bookRepo, never()).save(mockBook);
    }
    
    @Test
    void addBooks_Successfully(){
        Book book1 = Book.builder()
                .bookId(2)
                .title("Book1")
                .author("Author1")
                .isbn("ISBN1")
                .genre("Genre1")
                .isAvailable(true)
                .quantity(2L)
                .build();
        
        Book book2 = Book.builder()
                .bookId(3)
                .title("Book2")
                .author("Author2")
                .isbn("ISBN2")
                .genre("Genre2")
                .isAvailable(true)
                .quantity(3L)
                .build();
        
        bookService.addBooks(List.of(book1, book2));
        
        verify(bookRepo).saveAll(List.of(book1, book2));
    }
    
    @Test
    void addBooks_ShouldThrowException_WhenQuantityIsNegative(){
        Book book1 = Book.builder()
                .bookId(2)
                .title("Book1")
                .author("Author1")
                .isbn("ISBN1")
                .genre("Genre1")
                .isAvailable(true)
                .quantity(2L)
                .build();
        
        Book book2 = Book.builder()
                .bookId(3)
                .title("Book2")
                .author("Author2")
                .isbn("ISBN2")
                .genre("Genre2")
                .isAvailable(true)
                .quantity(-3L)
                .build();
        
        IllegalArgumentException IllegalArgumentException =
                assertThrows(IllegalArgumentException.class, () -> bookService.addBooks(List.of(book1, book2)));
        
        assertEquals("One or more books have a negative quantity", IllegalArgumentException.getMessage());
        verify(bookRepo, never()).saveAll(List.of(book1, book2));
    }
    
    @Test
    void updateBook_Successfully(){
        Book updatedBook = Book.builder()
                .bookId(1)
                .title("Updated Book")
                .author("Updated Author")
                .genre("Updated Genre")
                .build();
        
        when(bookRepo.findById(mockBook.getBookId())).thenReturn(Optional.of(mockBook));
        
        bookService.updateBook(mockBook.getBookId(), updatedBook);
        
        assertEquals(updatedBook.getTitle(), mockBook.getTitle());
        assertEquals(updatedBook.getAuthor(), mockBook.getAuthor());
        assertEquals(updatedBook.getGenre(), mockBook.getGenre());
        verify(bookRepo).save(mockBook);
    }
    
    @Test
    void updateBook_ShouldThrowException_WhenBookNotFound(){
        Book updatedBook = Book.builder()
                .bookId(1)
                .title("Updated Book")
                .author("Updated Author")
                .genre("Updated Genre")
                .build();
        
        when(bookRepo.findById(mockBook.getBookId())).thenReturn(Optional.empty());
        
        ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class,
                () -> bookService.updateBook(mockBook.getBookId(), updatedBook));
        
        assertEquals("Book could not be found", resourceNotFoundException.getMessage());
        verify(bookRepo, never()).save(mockBook);
    }
    
    @Test
    void updateBookQuantity_Successfully(){
        when(bookRepo.findById(mockBook.getBookId())).thenReturn(Optional.of(mockBook));
        
        bookService.updateBookQuantity(mockBook.getBookId(), 3L);
        
        assertEquals(3L, mockBook.getQuantity());
        verify(bookRepo).save(mockBook);
    }
    
    @Test
    void updateBookQuantity_ShouldThrowException_WhenQuantityIsNegative(){
        when(bookRepo.findById(mockBook.getBookId())).thenReturn(Optional.of(mockBook));
        
        IllegalArgumentException IllegalArgumentException =
                assertThrows(IllegalArgumentException.class, () -> bookService.updateBookQuantity(mockBook.getBookId(), -3L));
        
        assertEquals("Insufficient book quantity", IllegalArgumentException.getMessage());
        verify(bookRepo, never()).save(mockBook);
    }
    
    @Test
    void deleteBook_Successfully(){
        when(bookRepo.findById(mockBook.getBookId())).thenReturn(Optional.of(mockBook));
        
        bookService.deleteBook(mockBook.getBookId());

        assertFalse(mockBook.isAvailable());
        verify(bookRepo).save(mockBook);
    }
}
