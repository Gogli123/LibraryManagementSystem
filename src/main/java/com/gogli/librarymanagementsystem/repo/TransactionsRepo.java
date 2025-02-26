package com.gogli.librarymanagementsystem.repo;

import com.gogli.librarymanagementsystem.models.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionsRepo extends JpaRepository<Transactions, Long> {
}
