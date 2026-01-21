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
public class SalaryAdjustment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "payroll_period_id")
    private PayrollPeriod payrollPeriod;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(precision = 15, scale = 2)
    private BigDecimal amount;

    private String type; // BONUS, FINE, ADVANCE, BHXH_BENEFIT

    private String reason;

    private Boolean isTaxable = true;

    private Boolean isNetOnly = false;
}