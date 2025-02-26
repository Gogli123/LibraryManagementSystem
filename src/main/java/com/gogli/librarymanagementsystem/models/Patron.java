package com.gogli.librarymanagementsystem.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Patron {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long patronId;
    
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String phoneNumber;
    private MembershipStatus membershipStatus;
    private boolean isActive;
    private LocalDateTime createdAt = LocalDateTime.now();
}
