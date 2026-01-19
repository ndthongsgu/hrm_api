package com.hrm.hrm_api.modules.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "employees")
@Data
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private LocalDate dateOfBirth;
    private String address;
    private String phone;
    private String email;
    private String position;
    private String department;
    private LocalDate hireDate;
    private boolean active = true;
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;
}
