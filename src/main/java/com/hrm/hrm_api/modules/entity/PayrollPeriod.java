package com.hrm.hrm_api.modules.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "payroll_periods")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayrollPeriod extends BaseEntity {
    // Inherit id, timestamps and audit fields from BaseEntity

    private String name;

    private LocalDate startDate;
    private LocalDate endDate;

    private Float standardWorkingDays;

    private Status status = Status.PENDING; // e.g., "APPROVED", "REJECTED", "PENDING", "LOCKED"

    public enum Status {
        APPROVED, REJECTED, PENDING, LOCKED
    }
}