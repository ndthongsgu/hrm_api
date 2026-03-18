package com.hrm.hrm_api.modules.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @JsonBackReference("employee-identities")
    private Employee employee;

    private String identityValue;

    private String imageUrl;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}