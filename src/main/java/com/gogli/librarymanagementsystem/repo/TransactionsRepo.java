package com.gogli.librarymanagementsystem.repo;

import com.gogli.librarymanagementsystem.models.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionsRepo extends JpaRepository<Transactions, Long> {
    
    @Query("SELECT t FROM Transactions t JOIN FETCH t.book JOIN FETCH t.patron")
    List<Transactions> findAllTransactions();

    @Query("SELECT t FROM Transactions t JOIN FETCH t.book JOIN FETCH t.patron WHERE t.patron.patronId = :patronId")
    List<Transactions> findAllByPatronId(long patronId);

    @Query("SELECT t FROM Transactions t JOIN FETCH t.patron JOIN FETCH t.book WHERE t.dueDate < CURRENT_TIMESTAMP AND t.returnedAt IS NULL")
    List<Transactions> findOverdueTransactions();

    @Query("SELECT t FROM Transactions t JOIN FETCH t.book JOIN FETCH t.patron WHERE t.transactionId = :id")
    Optional<Transactions> findByIdWithDetails(@Param("id") long id);

    @Query("SELECT t FROM Transactions t JOIN FETCH t.book JOIN FETCH t.patron WHERE t.patron.patronId = :patronId AND t.returnedAt IS NULL")
    List<Transactions> findActiveTransactionsByPatronId(@Param("patronId") long patronId);

    @Query("SELECT t FROM Transactions t JOIN FETCH t.book JOIN FETCH t.patron WHERE t.book.bookId = :bookId")
    List<Transactions> findAllByBookIdWithDetails(@Param("bookId") long bookId);
}

 
