package com.example.bankmanagement.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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
                            Authentication authentication) {

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
            return "redirect:/dashboard";
        }

        return "login";
    }
}