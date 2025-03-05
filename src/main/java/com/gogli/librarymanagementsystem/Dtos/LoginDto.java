package com.gogli.librarymanagementsystem.Dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginDto {
    
    @NotEmpty
    private String username;
    
    @NotEmpty
    private String password;
}
