package com.hrm.hrm_api.modules.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee extends BaseEntity {
    // Inherit id, timestamps and audit fields from BaseEntity

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "employee_code", unique = true)
    private String employeeCode;

    private String email;
    private String phone;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne
    @JoinColumn(name = "position_id")
    private Position position;

    @Column(name = "tax_code")
    private String taxCode;

    @Column(name = "bank_account")
    private String bankAccount;

    @Column(name = "bank_name")
    private String bankName;

    private String status; // ACTIVE, RESIGNED

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<Dependent> dependents;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<Contract> contracts;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<EmployeeIdentity> identities;
}
