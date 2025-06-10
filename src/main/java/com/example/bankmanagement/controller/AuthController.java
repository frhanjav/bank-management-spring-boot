package com.example.bankmanagement.controller;

import org.springframework.security.core.Authentication; // Import Authentication
import org.springframework.security.core.GrantedAuthority; // Import GrantedAuthority
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model,
                            Authentication authentication) { // Inject Authentication object

        // --- Add this check ---
        // If the user is already authenticated, redirect them to their dashboard
        if (authentication != null && authentication.isAuthenticated()) {
            // Use the same role-based redirection logic as DashboardController/RootController
            for (GrantedAuthority auth : authentication.getAuthorities()) {
                if ("ROLE_MANAGER".equals(auth.getAuthority())) {
                    return "redirect:/manager/dashboard";
                } else if ("ROLE_STAFF".equals(auth.getAuthority())) {
                    return "redirect:/staff/dashboard";
                } else if ("ROLE_CUSTOMER".equals(auth.getAuthority())) {
                    return "redirect:/customer/dashboard";
                }
            }
            // Fallback if authenticated but no specific role dashboard found
            // Redirecting to /dashboard which will handle further redirection
            return "redirect:/dashboard";
        }
        // --- End of check ---

        return "login"; // Renders login.html
    }

    // Spring Security handles POST /login
    // Spring Security handles /logout via SecurityConfig
}