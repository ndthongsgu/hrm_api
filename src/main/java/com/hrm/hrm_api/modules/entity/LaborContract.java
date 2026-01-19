package com.hrm.hrm_api.modules.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "labor_contracts")
@Data
public class LaborContract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private String contractType; // e.g., "Definite", "Indefinite"
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal basicSalary;
    private String terms;
}
