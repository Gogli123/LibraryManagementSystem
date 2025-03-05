package com.gogli.librarymanagementsystem.Dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatePasswordRequestDto {
    @NotEmpty
    private String oldPassword;
    @NotEmpty
    private String newPassword;
}