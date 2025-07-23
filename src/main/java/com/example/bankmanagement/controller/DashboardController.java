package com.example.bankmanagement.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String defaultAfterLogin(Authentication authentication) {
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
        return "redirect:/login?error=true";
    }
}
