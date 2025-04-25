package com.example.bankmanagement.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {

    // Handles requests to the root path "/"
    @GetMapping("/")
    public String handleRootPath(Authentication authentication) {
        // Use the same logic as the DashboardController to redirect based on role
        if (authentication != null && authentication.isAuthenticated()) {
            for (GrantedAuthority auth : authentication.getAuthorities()) {
                if ("ROLE_MANAGER".equals(auth.getAuthority())) {
                    return "redirect:/manager/dashboard";
                } else if ("ROLE_STAFF".equals(auth.getAuthority())) {
                    return "redirect:/staff/dashboard";
                } else if ("ROLE_CUSTOMER".equals(auth.getAuthority())) {
                    return "redirect:/customer/dashboard";
                }
            }
        }
        // If not authenticated or no specific role dashboard, redirect to login
        return "redirect:/login";
    }
}