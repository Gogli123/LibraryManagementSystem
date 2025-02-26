package com.gogli.librarymanagementsystem.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long bookId;
    
    private String title;
    private String author;
    private String isbn;
    private String genre;
    private Long quantity;
    private LocalDateTime createdAt = LocalDateTime.now();
    private boolean isAvailable;
}
