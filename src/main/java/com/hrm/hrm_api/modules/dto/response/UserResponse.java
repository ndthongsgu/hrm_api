package com.hrm.hrm_api.modules.dto.response;

import java.util.List;
import java.util.UUID;

/**
 * DTO for User response to avoid Hibernate proxy serialization issues.
 * Using record for immutability and cleaner code.
 */
public record UserResponse(
    UUID id,
    String username,
    Boolean active,
    Boolean isAdmin,
    UUID employeeId,
    List<String> roles
) {}
