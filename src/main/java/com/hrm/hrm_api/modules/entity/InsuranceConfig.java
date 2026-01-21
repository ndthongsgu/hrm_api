package com.hrm.hrm_api.modules.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "insurance_configs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceConfig extends BaseEntity {
    // Inherit id, timestamps and audit fields from BaseEntity

    private String type; // SOCIAL, HEALTH, UNEMPLOYMENT

    @Column(precision = 5, scale = 2)
    private BigDecimal employeeRate;

    private Double companyRate;

    private String capBaseType; // BASE_SALARY or REGION_MIN

    private Integer capMultiplier; // e.g., 20
}