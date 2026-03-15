package com.hrm.hrm_api.modules.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "salary_adjustments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryAdjustment extends BaseEntity {
    // Inherit id, timestamps and audit fields from BaseEntity

    @ManyToOne
    @JoinColumn(name = "payroll_period_id")
    private PayrollPeriod payrollPeriod;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private Type type;

    private Boolean isTaxable = true;

    private Boolean isNetOnly = false;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING; // e.g., "APPROVED", "REJECTED", "PENDING", "LOCKED"

    public enum Status {
        APPROVED, PENDING, LOCKED
    }

    public enum Type {
        BONUS, FINE, ADVANCE, BHXH_BENEFIT,
    }
}