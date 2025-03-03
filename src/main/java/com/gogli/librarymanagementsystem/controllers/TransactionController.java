package com.gogli.librarymanagementsystem.controllers;

import com.gogli.librarymanagementsystem.Dtos.BorrowRequest;
import com.gogli.librarymanagementsystem.Dtos.RenewRequest;
import com.gogli.librarymanagementsystem.Dtos.ReturnRequest;
import com.gogli.librarymanagementsystem.models.ReportData;
import com.gogli.librarymanagementsystem.models.Transactions;
import com.gogli.librarymanagementsystem.services.TransactionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionsService transactionsService;

    @PostMapping("/borrow")
    public ResponseEntity<?> borrowBook(@RequestBody BorrowRequest borrowRequest) {

        Transactions transaction = transactionsService
                .borrowBook(borrowRequest.getPatronId(), borrowRequest.getBookId(),
                        borrowRequest.getStartDate(), borrowRequest.getEndDate());

        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

    @PostMapping("/return")
    public ResponseEntity<?> returnBook(@RequestBody ReturnRequest returnRequest) {

        transactionsService.returnBook(returnRequest.getPatronId(), returnRequest.getBookId());
        return ResponseEntity.ok("Book returned successfully");
    }

    @GetMapping()
    public ResponseEntity<List<Transactions>> getAllTransactions() {
        return ResponseEntity.ok(transactionsService.getAllTransactions());
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<Transactions> getTransactionById(@PathVariable long transactionId) {
        return ResponseEntity.ok(transactionsService.getTransactionById(transactionId));
    }

    @GetMapping("/patron/active/{patronId}")
    public ResponseEntity<List<Transactions>> getPatronActiveBorrowings(@PathVariable long patronId) {
        return ResponseEntity.ok(transactionsService.getPatronActiveBorrowings(patronId));
    }

    @PutMapping("/renew/{transactionId}")
    public ResponseEntity<Transactions> renewBook(
            @PathVariable long transactionId,
            @RequestBody RenewRequest RenewRequest) {
        return ResponseEntity.ok(transactionsService.renewBook(transactionId, RenewRequest));
    }

    @GetMapping("/book/history/{bookId}")
    public ResponseEntity<List<Transactions>> getBookBorrowingHistory(@PathVariable long bookId) {
        return ResponseEntity.ok(transactionsService.getBookBorrowingHistory(bookId));
    }

    @GetMapping("/data/report")
    public ResponseEntity<ReportData> getBookBorrowingHistory() {
        return ResponseEntity.ok(transactionsService.generateReports());
    }
}