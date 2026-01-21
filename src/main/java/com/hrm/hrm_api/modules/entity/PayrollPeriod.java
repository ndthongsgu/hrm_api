package com.hrm.hrm_api.modules.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    private Boolean isLocked = false;

    @CreationTimestamp
    private LocalDateTime createdAt;
}