package com.hrm.hrm_api.modules.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "positions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Position extends BaseEntity {
    // Inherit id, timestamps and audit fields from BaseEntity

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    private String name;

    private String code;

    @Column(precision = 15, scale = 2)
    private BigDecimal standardResponsibilityAllowance;
}