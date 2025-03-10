package com.gogli.librarymanagementsystem.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patron {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long patronId;

    @NotBlank(message = "First name is required")
    private String firstName;
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email
    private String emailAddress;
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    
    @NotNull(message = "Membership status is required")
    @Enumerated(EnumType.ORDINAL)
    private MembershipStatus membershipStatus;
    private LocalDateTime createdAt = LocalDateTime.now();
}
