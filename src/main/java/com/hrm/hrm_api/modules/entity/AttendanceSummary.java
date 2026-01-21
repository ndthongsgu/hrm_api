package com.hrm.hrm_api.modules.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "attendance_summaries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceSummary extends BaseEntity {
    // Inherit id, timestamps and audit fields from BaseEntity

    @ManyToOne
    @JoinColumn(name = "payroll_period_id")
    private PayrollPeriod payrollPeriod;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private Float actualWorkingDays;
    private Float paidLeaveDays;
    private Float unpaidLeaveDays;
    private Float overtimeHours;
}