package com.gogli.librarymanagementsystem.services;

import com.gogli.librarymanagementsystem.exceptions.ResourceNotFoundException;
import com.gogli.librarymanagementsystem.models.Librarian;
import com.gogli.librarymanagementsystem.models.UserPrincipal;
import com.gogli.librarymanagementsystem.repo.LibrarianRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailService implements UserDetailsService {
    
    private final LibrarianRepo repo;
    
    public MyUserDetailService(LibrarianRepo repo){
        this.repo = repo;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Librarian librarian = repo.findByUsername(username);
        
        if (librarian == null) {
            throw new ResourceNotFoundException("Can not find specified librarian");
        }
        
        return new UserPrincipal(librarian);
    }
}
