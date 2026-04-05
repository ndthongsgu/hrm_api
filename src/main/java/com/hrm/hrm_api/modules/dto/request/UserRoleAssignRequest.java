package com.hrm.hrm_api.modules.dto.request;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UserRoleAssignRequest {
    private List<UUID> roleIds;
}
