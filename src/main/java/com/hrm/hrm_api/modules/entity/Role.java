package com.hrm.hrm_api.modules.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    // JSON map: {"EMPLOYEE": ["READ","CREATE"], "PAYROLL": ["READ"]}
    @Column(name = "permissions_json", columnDefinition = "jsonb")
    private String permissionsJson;
} 
