package com.example.bankmanagement.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String targetUrl = determineTargetUrl(auth);

        if (targetUrl != null) {
            log.warn("User: {} attempted to access the protected URL: {} - Redirecting to {}",
                    auth != null ? auth.getName() : "Unknown",
                    request.getRequestURI(),
                    targetUrl);
            response.sendRedirect(request.getContextPath() + targetUrl);
        } else {
            log.error("User: {} has no recognizable role or authentication is null. Access denied for {}",
                    auth != null ? auth.getName() : "Unknown", request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Access Denied: You do not have permission to access this page.");
        }
    }

    protected String determineTargetUrl(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "/login";
        }

        for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
            String authorityName = grantedAuthority.getAuthority();
            if ("ROLE_MANAGER".equals(authorityName)) {
                return "/manager/dashboard";
            } else if ("ROLE_STAFF".equals(authorityName)) {
                return "/staff/dashboard";
            } else if ("ROLE_CUSTOMER".equals(authorityName)) {
                return "/customer/dashboard";
            }
        }
        return null;
    }
}