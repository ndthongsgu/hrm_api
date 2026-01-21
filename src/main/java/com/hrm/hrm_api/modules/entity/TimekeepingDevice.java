package com.hrm.hrm_api.modules.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "timekeeping_devices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimekeepingDevice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceName;

    @Column(unique = true)
    private String deviceCode;

    private String deviceType; // TABLET_KIOSK, AI_CAMERA, QR_SCANNER

    private String ipAddress;

    private String location;

    private String status = "ACTIVE";

    @CreationTimestamp
    private LocalDateTime createdAt;
}