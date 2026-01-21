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
public class AllowanceType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Boolean isTaxable;

    private Boolean isInsuranceBased;
}