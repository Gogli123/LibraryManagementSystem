package com.gogli.librarymanagementsystem;

import com.gogli.librarymanagementsystem.Dtos.RenewRequest;
import com.gogli.librarymanagementsystem.models.Book;
import com.gogli.librarymanagementsystem.models.MembershipStatus;
import com.gogli.librarymanagementsystem.models.Patron;
import com.gogli.librarymanagementsystem.models.Transactions;
import com.gogli.librarymanagementsystem.repo.BookRepo;
import com.gogli.librarymanagementsystem.repo.TransactionsRepo;
import com.gogli.librarymanagementsystem.services.BookService;
import com.gogli.librarymanagementsystem.services.PatronService;
import com.gogli.librarymanagementsystem.services.TransactionsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionsRepo transactionsRepo;

    @Mock
    private BookRepo bookRepo;

    @Mock
    private BookService bookService;

    @Mock
    private PatronService patronService;
    
    @InjectMocks
    private TransactionsService transactionsService;
    
    private Book mockBook;

    private Patron mockPatron;

    private Transactions mockTransactions;

    @BeforeEach
    void setUp() {
        mockBook = Book.builder()
                .bookId(1)
                .title("Book")
                .author("Author")
                .isbn("ISBN")
                .genre("Genre")
                .quantity(5L)
                .isAvailable(true)
                .build();

        mockPatron = Patron.builder()
                .patronId(1)
                .firstName("patron")
                .lastName("patron")
                .emailAddress("patron@gmail.com")
                .phoneNumber("1234567890")
                .membershipStatus(MembershipStatus.ACTIVE)
                .build();
    }

    @Test
    void renewBook_Successfully() {
        mockTransactions = Transactions.builder()
                .transactionId(1)
                .book(mockBook)
                .patron(mockPatron)
                .returnedAt(null)
                .build();

        RenewRequest renewRequest = RenewRequest.builder()
                .newDueDate(LocalDateTime.now().plusDays(7)).build();

        when(transactionsRepo.findByIdWithDetails(1)).thenReturn(Optional.of(mockTransactions));

        transactionsService.renewBook(mockTransactions.getTransactionId(), renewRequest);

        assertEquals(mockTransactions.getDueDate(), renewRequest.getNewDueDate());
        verify(transactionsRepo).save(mockTransactions);
    }

    @Test
    void renewBook_AlreadyReturned() {
        mockTransactions = Transactions.builder()
                .transactionId(1)
                .book(mockBook)
                .patron(mockPatron)
                .returnedAt(LocalDateTime.now())
                .build();

        RenewRequest renewRequest = RenewRequest.builder()
                .newDueDate(LocalDateTime.now().plusDays(7)).build();

        when(transactionsRepo.findByIdWithDetails(1)).thenReturn(Optional.of(mockTransactions));

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> transactionsService.renewBook(mockTransactions.getTransactionId(), renewRequest));

        assertEquals("Cannot renew already returned book", illegalArgumentException.getMessage());
        verify(transactionsRepo, never()).save(mockTransactions);
    }

    @Test
    void renewBook_ShouldThrowException_BookWithPastDueDate() {
        mockTransactions = Transactions.builder()
                .transactionId(1)
                .book(mockBook)
                .patron(mockPatron)
                .returnedAt(null)
                .build();

        RenewRequest renewRequest = RenewRequest.builder()
                .newDueDate(LocalDateTime.now().minusDays(7)).build();

        when(transactionsRepo.findByIdWithDetails(1)).thenReturn(Optional.of(mockTransactions));

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> transactionsService.renewBook(mockTransactions.getTransactionId(), renewRequest));

        assertEquals("New due date cannot be in the past", illegalArgumentException.getMessage());
        verify(transactionsRepo, never()).save(mockTransactions);
    }

    @Test
    void borrowBook_Successfully() {
        LocalDateTime startDate = LocalDateTime.now().plusMinutes(1);
        LocalDateTime endDate = startDate.plusDays(7);

        when(patronService.getPatronById(mockPatron.getPatronId())).thenReturn(mockPatron);
        when(bookService.getBookById(mockBook.getBookId())).thenReturn(mockBook);

        mockTransactions = Transactions.builder()
                .book(mockBook)
                .patron(mockPatron)
                .borrowedAt(startDate)
                .dueDate(endDate)
                .returnedAt(null)
                .build();

        when(transactionsRepo.save(any(Transactions.class))).thenReturn(mockTransactions);

        transactionsService.borrowBook(mockPatron.getPatronId(), mockBook.getBookId(), startDate, endDate);

        assertEquals(4, mockBook.getQuantity());
        verify(transactionsRepo).save(mockTransactions);
        verify(bookRepo).save(mockBook);
    }

    @Test
    void borrowBook_ShouldThrowException_InvalidDateRange() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = startDate.minusDays(7);

        when(patronService.getPatronById(mockPatron.getPatronId())).thenReturn(mockPatron);
        when(bookService.getBookById(mockBook.getBookId())).thenReturn(mockBook);

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> transactionsService.borrowBook(mockPatron.getPatronId(), mockBook.getBookId(), startDate, endDate));

        assertEquals("Invalid date range for borrowing", illegalArgumentException.getMessage());
        verify(transactionsRepo, never()).save(mockTransactions);
        verify(bookRepo, never()).save(mockBook);
    }

    @Test
    void returnBook_Successfully() {
        mockTransactions = Transactions.builder()
                .transactionId(1)
                .book(mockBook)
                .patron(mockPatron)
                .returnedAt(null)
                .build();
        Long bookQuantity = mockBook.getQuantity();

        when(transactionsRepo.findAllByPatronId(mockPatron.getPatronId())).thenReturn(List.of(mockTransactions));
        when(bookService.getBookById(mockBook.getBookId())).thenReturn(mockBook);

        transactionsService.returnBook(mockPatron.getPatronId(), mockBook.getBookId());

        assertNotNull(mockTransactions.getReturnedAt());
        assertEquals(bookQuantity + 1, mockBook.getQuantity());
        verify(transactionsRepo).save(mockTransactions);
        verify(bookRepo).save(mockBook);
    }
    
    @Test
    void returnBook_ShouldThrowException_AlreadyReturned() {
        mockTransactions = Transactions.builder()
                .transactionId(1)
                .book(mockBook)
                .patron(mockPatron)
                .returnedAt(LocalDateTime.now())
                .build();
        
        when(transactionsRepo.findAllByPatronId(mockPatron.getPatronId())).thenReturn(List.of(mockTransactions));
        when(bookService.getBookById(mockBook.getBookId())).thenReturn(mockBook);

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> transactionsService.returnBook(mockPatron.getPatronId(), mockBook.getBookId()));
        
        assertEquals("Book already returned or not borrowed by this patron", illegalArgumentException.getMessage());
        verify(transactionsRepo, never()).save(mockTransactions);
        verify(bookRepo, never()).save(mockBook);
    }
}
