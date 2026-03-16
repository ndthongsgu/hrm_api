package com.hrm.hrm_api.modules.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "dependents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dependent extends BaseEntity {
    // Inherit id, timestamps and audit fields from BaseEntity

    @ManyToOne
    @JoinColumn(name = "employee_id")
    @JsonBackReference("employee-dependents")
    private Employee employee;

    private String fullName;
    private String relationship;
    private LocalDate birthDate;
    private Boolean isQualified = true;
}