package com.gogli.librarymanagementsystem.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long tokenId;
    
    private String token;
    private boolean expired;
    private boolean revoked;

    @ManyToOne
    @JoinColumn(name = "librarian_id")
    private Librarian librarian;
}
