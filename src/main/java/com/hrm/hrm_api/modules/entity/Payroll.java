package com.hrm.hrm_api.modules.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "payrolls")
@Data
public class Payroll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private Integer month;
    private Integer year;
    private BigDecimal grossSalary;
    private BigDecimal bhxhDeduction;
    private BigDecimal bhytDeduction;
    private BigDecimal bhtnDeduction;
    private BigDecimal taxDeduction;
    private BigDecimal netSalary;
    private Integer workingDays;
}
