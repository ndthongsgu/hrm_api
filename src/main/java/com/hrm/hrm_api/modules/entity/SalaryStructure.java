package com.hrm.hrm_api.modules.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "salary_structures")
@Data
public class SalaryStructure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private BigDecimal basicSalary;
    private BigDecimal housingAllowance;
    private BigDecimal transportAllowance;
    private BigDecimal otherAllowances;
    // Deductions can be calculated, but perhaps store rates
    private BigDecimal bhxhRate = new BigDecimal("0.08"); // 8%
    private BigDecimal bhytRate = new BigDecimal("0.015"); // 1.5%
    private BigDecimal bhtnRate = new BigDecimal("0.01"); // 1%
}
