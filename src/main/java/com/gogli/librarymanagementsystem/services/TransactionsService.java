package com.gogli.librarymanagementsystem.services;

import com.gogli.librarymanagementsystem.Dtos.RenewRequest;
import com.gogli.librarymanagementsystem.exceptions.ResourceNotFoundException;
import com.gogli.librarymanagementsystem.models.Book;
import com.gogli.librarymanagementsystem.models.Patron;
import com.gogli.librarymanagementsystem.models.ReportData;
import com.gogli.librarymanagementsystem.models.Transactions;
import com.gogli.librarymanagementsystem.repo.BookRepo;
import com.gogli.librarymanagementsystem.repo.TransactionsRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class TransactionsService {

    private final TransactionsRepo transactionsRepo;
    private final BookRepo bookRepo;
    private final BookService bookService;
    private final PatronService patronService;
    private final NotificationService notificationService;

    public List<Transactions> getAllTransactions() {
        return transactionsRepo.findAllTransactions();
    }

    @Transactional(readOnly = true)
    public Transactions getTransactionById(long transactionId) {
        return transactionsRepo.findByIdWithDetails(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
    }

    @Transactional(readOnly = true)
    public List<Transactions> getPatronActiveBorrowings(long patronId) {
        patronService.getPatronById(patronId);
        return transactionsRepo.findActiveTransactionsByPatronId(patronId);
    }

    @Transactional
    public Transactions renewBook(long transactionId, RenewRequest renewRequest) {
        Transactions transaction = getTransactionById(transactionId);

        if (transaction.getReturnedAt() != null) {
            throw new IllegalArgumentException("Cannot renew already returned book");
        }

        if (renewRequest.getNewDueDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("New due date cannot be in the past");
        }

        transaction.setDueDate(renewRequest.getNewDueDate());
        return transactionsRepo.save(transaction);
    }

    @Transactional(readOnly = true)
    public List<Transactions> getBookBorrowingHistory(long bookId) {
        bookService.getBookById(bookId);
        return transactionsRepo.findAllByBookIdWithDetails(bookId);
    }

    @Transactional
    public Transactions borrowBook(long patronId, long bookId,
                                   LocalDateTime startDate, LocalDateTime endDate) {
        Patron patron = patronService.getPatronById(patronId);
        Book book = bookService.getBookById(bookId);

        if (startDate.isBefore(LocalDateTime.now()) ||
                endDate.isBefore(LocalDateTime.now()) ||
                endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Invalid date range for borrowing");
        }

        if (checkForBookQuantity(book)) {
            Transactions transactions = Transactions.builder()
                    .book(book)
                    .patron(patron)
                    .borrowedAt(startDate)
                    .dueDate(endDate)
                    .returnedAt(null)
                    .build();

            transactionsRepo.save(transactions);
            book.setQuantity(book.getQuantity() - 1);

            if (book.getQuantity() == 0) book.setAvailable(false);
            bookRepo.save(book);

            return transactions;
        } else throw new RuntimeException("Could not complete a borrowing transaction");
    }

    @Transactional
    public void returnBook(long patronId, long bookId) {
        Book book = bookService.getBookById(bookId);
        List<Transactions> transactions = transactionsRepo.findAllByPatronId(patronId);

        transactions = transactions.stream()
                .filter(tr -> tr.getReturnedAt() == null)
                .filter(tr -> tr.getBook().getBookId() == bookId)
                .toList();

        if (transactions.isEmpty()) {
            throw new IllegalArgumentException("Book already returned or not borrowed by this patron");
        }

        Transactions activeTransaction = transactions.getFirst();
        activeTransaction.setReturnedAt(LocalDateTime.now());
        transactionsRepo.save(activeTransaction);
        
        book.setQuantity(book.getQuantity() + 1);
        if (!book.isAvailable()) {
            book.setAvailable(true);
        }
        bookRepo.save(book);
    }

    private void notifyOverduePatrons() {
        List<Transactions> overdueTransactions = transactionsRepo.findOverdueTransactions();
        notificationService.sendOverdueNotifications(overdueTransactions);
    }

    @Scheduled(cron = "0 0 10 * * ?") // run daily at 10 AM
    public void dailyOverdueCheck() {
        notifyOverduePatrons();
    }

    private boolean checkForBookQuantity(Book book) {
        long quantity = book.getQuantity();
        return quantity >= 1;
    }

    @Scheduled(cron = "0 0 10 * * ?") // run daily at 10 AM
    public ReportData generateReports() {
        List<Transactions> overdueTransactions = transactionsRepo.findOverdueTransactions();
        List<Transactions> allTransactions = getAllTransactions();

        Map<Long, List<Transactions>> overdueTransactionsByPatron = allTransactions.stream()
                .collect(Collectors.groupingBy(t -> t.getPatron().getPatronId()));

        return new ReportData(overdueTransactions, overdueTransactionsByPatron);
    }
}