package com.gogli.librarymanagementsystem.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username"}),
        @UniqueConstraint(columnNames = {"email"})
})
public class Librarian {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long librarianId;
    
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private boolean isActive = true;
}
