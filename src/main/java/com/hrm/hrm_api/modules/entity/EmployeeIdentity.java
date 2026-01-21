package com.hrm.hrm_api.modules.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "employee_identities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeIdentity extends BaseEntity {
    // Inherit id, timestamps and audit fields from BaseEntity

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private String identityType; // QR_CODE, FACE_VECTOR, RFID_CARD

    private String identityValue;

    private String imageUrl;

    private Boolean isActive = true;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}