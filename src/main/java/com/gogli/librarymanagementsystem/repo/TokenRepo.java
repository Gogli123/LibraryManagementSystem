package com.gogli.librarymanagementsystem.repo;

import com.gogli.librarymanagementsystem.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepo extends JpaRepository<Token, Long> {

    @Query("""
            select t from Token t\s
            inner join Librarian l
            on t.librarian.librarianId = l.librarianId
            where l.librarianId = :librarianId\s
            and (t.expired = false or t.revoked = false)
           \s""")
    List<Token> findAllValidTokensByLibrarian(long librarianId);

    Optional<Token> findByToken(String token);
}
