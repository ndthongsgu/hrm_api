package com.hrm.hrm_api.modules.mapper;

import com.hrm.hrm_api.modules.dto.response.UserResponse;
import com.hrm.hrm_api.modules.entity.User;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper to convert User entity to UserResponse DTO.
 * This prevents Hibernate proxy serialization issues and circular references.
 * 
 * IMPORTANT: This mapper should be called within a @Transactional context
 * to properly initialize lazy-loaded relationships.
 */
@Component
public class UserMapper {

    /**
     * Convert User entity to UserResponse DTO.
     * Safely extracts role names without triggering lazy loading issues.
     * 
     * @param user User entity (must be loaded within a transaction)
     * @return UserResponse DTO
     */
    public static UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        // Use the existing getRoles() method which returns Set<Role>
        // This method is safe to use within a transaction context
        List<String> roleNames = List.of();
        List<UUID> roleIds = List.of();
        try {
            // Initialize the userRoles collection if it's a proxy
            if (user.getUserRoles() != null) {
                Hibernate.initialize(user.getUserRoles());

                // Get role names using the helper method
                var roles = user.getRoles().stream()
                        .peek(role -> {
                            // Initialize role if it's a proxy
                            Hibernate.initialize(role);
                        })
                        .collect(Collectors.toList());

                roleNames = roles.stream().map(role -> role.getName()).collect(Collectors.toList());
                roleIds = roles.stream().map(role -> role.getId()).collect(Collectors.toList());
            }
        } catch (Exception e) {
            // If lazy loading fails (outside transaction), return empty list
            // This should not happen if called within @Transactional method
            roleNames = List.of();
            roleIds = List.of();
        }

        // Safely get employee ID
        UUID employeeId = null;
        try {
            if (user.getEmployee() != null) {
                Hibernate.initialize(user.getEmployee());
                employeeId = user.getEmployee().getId();
            }
        } catch (Exception e) {
            // Employee is optional, so ignore if not initialized
        }

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getActive(),
                user.getIsAdmin(),
                employeeId,
                roleNames,
                roleIds
        );
    }
}
