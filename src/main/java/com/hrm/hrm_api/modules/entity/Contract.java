package com.hrm.hrm_api.modules.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "contracts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contract extends BaseEntity {
    // Inherit id, timestamps and audit fields from BaseEntity

    @ManyToOne
    @JoinColumn(name = "employee_id")
    @JsonBackReference("employee-contracts")
    private Employee employee;

    private String contractNumber;

    @Column(precision = 15, scale = 2)
    private BigDecimal baseSalary;

    @Column(precision = 15, scale = 2)
    private BigDecimal actualSalary;

    @ManyToOne
    @JoinColumn(name = "position_id")
    private Position position;

    private LocalDate startDate;
    private LocalDate endDate;

    private String status; // ACTIVE, EXPIRED

    private Boolean isNet;
}