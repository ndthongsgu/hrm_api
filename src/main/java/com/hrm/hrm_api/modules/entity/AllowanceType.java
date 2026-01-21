package com.hrm.hrm_api.modules.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "allowance_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllowanceType extends BaseEntity {
    // Inherit id, timestamps and audit fields from BaseEntity

    private String name;

    private Boolean isTaxable;

    private Boolean isInsuranceBased;
}