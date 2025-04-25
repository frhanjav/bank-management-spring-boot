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

@Component // Make it a Spring bean
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
            // Perform the redirect
            response.sendRedirect(request.getContextPath() + targetUrl);
        } else {
            // Fallback if user is somehow authenticated but has no recognizable role
            // Or if you want a generic error page in some cases
            log.error("User: {} has no recognizable role or authentication is null. Access denied for {}",
                    auth != null ? auth.getName() : "Unknown", request.getRequestURI());
            // You could redirect to login or show a generic error
            // response.sendRedirect(request.getContextPath() + "/error?message=Access Denied");
            // Or let the default handling proceed (which might show a basic 403 page)
            // For now, let's just send a 403 status directly if no role match
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Access Denied: You do not have permission to access this page.");
        }
    }

    /**
     * Determines the target URL based on the user's role.
     * @param authentication the current authentication object
     * @return the target URL (e.g., "/manager/dashboard") or null if no suitable role found
     */
    protected String determineTargetUrl(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "/login"; // Should not happen if AccessDenied is triggered, but safeguard
        }

        for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
            String authorityName = grantedAuthority.getAuthority();
            if ("ROLE_MANAGER".equals(authorityName)) {
                return "/manager/dashboard";
            } else if ("ROLE_STAFF".equals(authorityName)) {
                return "/staff/dashboard";
            } else if ("ROLE_CUSTOMER".equals(authorityName)) {
                // Only redirect active customers to their dashboard
                // Note: Checking activation status here might require service injection,
                // which complicates the handler. Simpler to let the dashboard controller handle inactive state.
                return "/customer/dashboard";
            }
        }
        // No specific role dashboard found
        return null;
    }
}