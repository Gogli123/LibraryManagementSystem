package com.gogli.librarymanagementsystem.mapper;

import com.gogli.librarymanagementsystem.Dtos.RegisterDto;
import com.gogli.librarymanagementsystem.models.Librarian;
import org.springframework.stereotype.Component;

@Component
public class LibrarianMapper {

    public Librarian toEntity(RegisterDto registerDto) {
        Librarian librarian = new Librarian();
        librarian.setFirstName(registerDto.getFirstName());
        librarian.setLastName(registerDto.getLastName());
        librarian.setUsername(registerDto.getUsername());
        librarian.setEmail(registerDto.getEmail());
        librarian.setPassword(registerDto.getPassword());
        return librarian;
    }

    public RegisterDto toDto(Librarian librarian) {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setFirstName(librarian.getFirstName());
        registerDto.setLastName(librarian.getLastName());
        registerDto.setUsername(librarian.getUsername());
        registerDto.setEmail(librarian.getEmail());
        registerDto.setPassword(librarian.getPassword());
        return registerDto;
    }
}
