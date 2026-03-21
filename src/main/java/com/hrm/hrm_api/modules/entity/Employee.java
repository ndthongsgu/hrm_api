package com.hrm.hrm_api.modules.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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
    @JsonAlias("code")
    private String employeeCode;

    private String email;
    @JsonAlias("phoneNumber")
    private String phone;

    @Column(name = "emergency_contact")
    private String emergencyContact;

    @Column(name = "emergency_phone")
    private String emergencyPhone;

    @Column(name = "social_insurance_number")
    private String socialInsuranceNumber;

    @Column(name = "health_insurance_number")
    private String healthInsuranceNumber;

    private String address;

    private String gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @Transient
    private UUID departmentId;

    @ManyToOne
    @JoinColumn(name = "position_id")
    private Position position;

    @Transient
    private UUID positionId;

    @Column(name = "tax_code")
    private String taxCode;

    @Column(name = "bank_account")
    private String bankAccount;

    @Column(name = "bank_name")
    private String bankName;

    private String status; // ACTIVE, RESIGNED

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    @JsonManagedReference("employee-dependents")
    private List<Dependent> dependents;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    @JsonManagedReference("employee-contracts")
    private List<Contract> contracts;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    @JsonManagedReference("employee-identities")
    private List<EmployeeIdentity> identities;

    // Vị trí chấm công của nhân viên
    @ManyToOne
    @JoinColumn(name = "office_location_id")
    private OfficeLocation officeLocation;
}
