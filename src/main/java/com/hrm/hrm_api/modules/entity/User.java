package com.hrm.hrm_api.modules.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {
    // Inherit id, timestamps and audit fields from BaseEntity

    @Column(unique = true, nullable = false)
    private String username;

    private String password;

    private Boolean active = true;

    private Boolean isAdmin = false;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee; // optional link to employee

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;
}