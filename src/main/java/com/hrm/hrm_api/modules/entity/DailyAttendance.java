package com.hrm.hrm_api.modules.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_attendances")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyAttendance extends BaseEntity {
    // Inherit id, timestamps and audit fields from BaseEntity

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "shift_id")
    private WorkShift shift;

    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    private Integer lateMinutes;
    private Integer earlyMinutes;
    private Float workingHours;

    @Enumerated(EnumType.STRING)
    private Status status; // OK, LATE, ABSENT, LEAVE, EARLY

    public enum Status {
        OK, LATE, ABSENT, LEAVE, EARLY
    }
}