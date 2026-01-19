package com.hrm.hrm_api.modules.entity;

import com.hrm.hrm_api.common.constants.PermissionAction;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "permissions")
@Data
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String module;
    private PermissionAction action;
    private String description;
}
