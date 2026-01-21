package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.EmployeeShiftAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmployeeShiftAssignmentRepository extends JpaRepository<EmployeeShiftAssignment, UUID> {
}