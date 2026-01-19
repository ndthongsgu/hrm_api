package com.hrm.hrm_api.modules.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "attendances")
@Data
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private LocalDate date;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private Double hoursWorked;
    private String status; // Present, Absent, Late, etc.
}
