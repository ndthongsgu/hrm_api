package com.hrm.hrm_api.modules.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "employee_shift_assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeShiftAssignment extends BaseEntity {
    // Inherit id, timestamps and audit fields from BaseEntity

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "shift_id")
    private WorkShift shift;

    private LocalDate startDate;
    private LocalDate endDate;
}