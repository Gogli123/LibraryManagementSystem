package com.gogli.librarymanagementsystem.services;

import com.gogli.librarymanagementsystem.exceptions.ResourceNotFoundException;
import com.gogli.librarymanagementsystem.models.Librarian;
import com.gogli.librarymanagementsystem.models.UserPrincipal;
import com.gogli.librarymanagementsystem.repo.LibrarianRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyUserDetailService implements UserDetailsService {
    
    private final LibrarianRepo repo;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Librarian librarian = repo.findByUsername(username);
        
        if (librarian == null) {
            throw new ResourceNotFoundException("Can not find specified librarian");
        }
        
        return new UserPrincipal(librarian);
    }
}
