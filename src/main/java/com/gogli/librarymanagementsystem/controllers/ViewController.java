package com.gogli.librarymanagementsystem.controllers;

import com.gogli.librarymanagementsystem.Dtos.LoginDto;
import com.gogli.librarymanagementsystem.config.CustomLogoutHandler;
import com.gogli.librarymanagementsystem.exceptions.AccountLockedException;
import com.gogli.librarymanagementsystem.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class ViewController {

    private final AuthenticationService authenticationService;
    private final CustomLogoutHandler logoutHandler;

    @GetMapping("/")
    public String root() {
        // Redirect root path to login
        return "redirect:/login";
    }

    @GetMapping("/index")
    public String home(Model model) {
        System.out.println("User is authenticated: " + SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
        return "index"; // Renders index.html
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("loginDto", new LoginDto());
        return "login"; // Changed to render auth/login.html
    }

    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute LoginDto loginDto, Model model) {
        try {
            authenticationService.authenticate(loginDto);
            // Redirect to the home page on success
            return "redirect:/index";
        } catch (AccountLockedException | AuthenticationException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        } catch (Exception e) {
            model.addAttribute("error", "Invalid username or password" + e.getMessage());
            return "login";
        }
    }
}

