package com.hrm.hrm_api.modules.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "time_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeLog extends BaseEntity {
    // Inherit id, timestamps and audit fields from BaseEntity

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "device_id")
    private TimekeepingDevice device;

    private LocalDateTime checkTime;

    private String type; // CHECK_IN, CHECK_OUT, UNKNOWN

    private String method; // QR, FACE_ID, GPS

    private Float confidenceScore;

    private String snapshotUrl;

    private Boolean isProcessed = false;
}