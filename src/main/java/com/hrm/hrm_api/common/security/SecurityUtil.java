package com.hrm.hrm_api.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    /**
     * Get the current authenticated username
     * @return username of the authenticated user, or "System" if not authenticated
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null) {
            return "System";
        }
        
        if (!authentication.isAuthenticated()) {
            return "System";
        }
        
        String principal = authentication.getName();
        
        // Handle empty or null principal
        if (principal == null || principal.trim().isEmpty()) {
            return "System";
        }
        
        // Skip system principals
        if (principal.equals("anonymousUser") || principal.equals("system")) {
            return "System";
        }
        
        return principal;
    }

    /**
     * Check if current user is authenticated
     */
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && 
               !authentication.getName().equals("anonymousUser");
    }
}
