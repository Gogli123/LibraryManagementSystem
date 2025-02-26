package com.gogli.librarymanagementsystem.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
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
