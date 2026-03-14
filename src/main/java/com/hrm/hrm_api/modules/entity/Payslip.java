package com.hrm.hrm_api.modules.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payslips")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payslip extends BaseEntity {
    // Inherit id, timestamps and audit fields from BaseEntity

    @ManyToOne
    @JoinColumn(name = "payroll_period_id")
    private PayrollPeriod payrollPeriod;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "contract_id")
    private Contract contract;

    // 1. Income
    @Column(precision = 15, scale = 2)
    private BigDecimal grossSalary;

    @Column(precision = 15, scale = 2)
    private BigDecimal salaryByWorkdays;

    @Column(precision = 15, scale = 2)
    private BigDecimal totalAllowance;

    @Column(precision = 15, scale = 2)
    private BigDecimal totalBonus;

    // 2. Insurance
    @Column(precision = 15, scale = 2)
    private BigDecimal bhxhSalaryBase;

    @Column(precision = 15, scale = 2)
    private BigDecimal bhxhAmount;

    @Column(precision = 15, scale = 2)
    private BigDecimal bhytAmount;

    @Column(precision = 15, scale = 2)
    private BigDecimal bhtnAmount;

    @Column(precision = 15, scale = 2)
    private BigDecimal socialInsuranceBenefitAmount;

    // 3. Tax
    @Column(precision = 15, scale = 2)
    private BigDecimal taxableIncome;

    @Column(precision = 15, scale = 2)
    private BigDecimal deductionPersonal;

    @Column(precision = 15, scale = 2)
    private BigDecimal deductionDependents;

    @Column(precision = 15, scale = 2)
    private BigDecimal assessableIncome;

    @Column(precision = 15, scale = 2)
    private BigDecimal taxAmount;

    // 4. Net
    @Column(precision = 15, scale = 2)
    private BigDecimal netSalary;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING; // e.g., "APPROVED", "REJECTED", "PENDING", "LOCKED"

    public enum Status {
        APPROVED, PENDING
    }

    private String reason;
}