package com.hrm.hrm_api.modules.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalTime;

@Entity
@Table(name = "work_shifts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkShift extends BaseEntity {
    // Inherit id, timestamps and audit fields from BaseEntity

    private String name;

    private LocalTime startTime;
    private LocalTime endTime;
    private LocalTime breakStart;
    private LocalTime breakEnd;

    private Integer lateGraceMinutes = 15;
    private Integer earlyLeaveGraceMinutes = 0;
}