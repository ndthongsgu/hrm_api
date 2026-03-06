package com.hrm.hrm_api.modules.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tax_brackets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaxBracket extends BaseEntity {
    // Inherit id, timestamps and audit fields from BaseEntity

    private String policyName;

    @Column(precision = 15, scale = 2)
    private BigDecimal minIncome;

    @Column(precision = 15, scale = 2)
    private BigDecimal maxIncome;

    private Double taxRate;

    @Column(precision = 15, scale = 2)
    private BigDecimal subtractAmount;

    private Boolean isActive = true;

    private LocalDate effectiveDate;
}