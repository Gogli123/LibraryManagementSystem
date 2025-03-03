package com.gogli.librarymanagementsystem.Dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePasswordRequestDto {
    @NotEmpty
    private String oldPassword;
    @NotEmpty
    private String newPassword;
}