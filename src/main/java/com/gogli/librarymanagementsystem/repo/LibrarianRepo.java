package com.gogli.librarymanagementsystem.repo;

import com.gogli.librarymanagementsystem.models.Librarian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LibrarianRepo extends JpaRepository<Librarian, Long> {
    
    Librarian findByUsername(String username);

    @Query("SELECT l FROM Librarian l WHERE l.email = :email")
    Librarian findByEmail(String email);
}
