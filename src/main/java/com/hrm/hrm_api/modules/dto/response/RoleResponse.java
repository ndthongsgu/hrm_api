package com.hrm.hrm_api.modules.dto.response;

import java.util.UUID;

/**
 * DTO for Role response to avoid Hibernate proxy serialization issues.
 * Using record for immutability and cleaner code.
 */
public record RoleResponse(
    UUID id,
    String name,
    String description,
    String permissionsJson
) {}
