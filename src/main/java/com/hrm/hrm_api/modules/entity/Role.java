package com.hrm.hrm_api.modules.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role extends BaseEntity {
    // Inherit id, timestamps and audit fields from BaseEntity

    private String name;
    private String description;

    // JSON map: {"EMPLOYEE": ["READ","CREATE"], "PAYROLL": ["READ"]}
    @Column(name = "permissions_json", columnDefinition = "text")
    private String permissionsJson;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<UserRole> userRoles = new HashSet<>();
} 
