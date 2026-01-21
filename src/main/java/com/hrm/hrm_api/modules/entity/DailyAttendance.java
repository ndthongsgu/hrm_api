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
public class DailyAttendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    private String status; // OK, LATE, ABSENT, LEAVE
}