package com.hrm.hrm_api.modules.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "departments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Department extends BaseEntity {
    // Inherit id, timestamps and audit fields from BaseEntity

    private String name;

    private String code;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Department parent;
}