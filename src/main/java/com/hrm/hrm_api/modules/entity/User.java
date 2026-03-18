package com.hrm.hrm_api.modules.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.Set;
import java.util.UUID;
import java.util.HashSet;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "userRoles")
@lombok.EqualsAndHashCode(callSuper = false, exclude = {"userRoles", "employee"})
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

    @Column(name = "employee_id", insertable = false, updatable = false)
    private UUID employeeId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference(value = "user-userRoles")
    private Set<UserRole> userRoles = new HashSet<>();

    public void addRole(Role role) {
        UserRole userRole = new UserRole(this, role);
        userRoles.add(userRole);
        if (role.getUserRoles() == null) {
            role.setUserRoles(new HashSet<>());
        }
        role.getUserRoles().add(userRole);
    }

    public void removeRole(Role role) {
        userRoles.removeIf(ur -> ur.getRole().equals(role));
        if (role.getUserRoles() != null) {
            role.getUserRoles().removeIf(ur -> ur.getUser().equals(this));
        }
    }

    public Set<Role> getRoles() {
        return userRoles.stream().map(UserRole::getRole).collect(Collectors.toSet());
    }
}