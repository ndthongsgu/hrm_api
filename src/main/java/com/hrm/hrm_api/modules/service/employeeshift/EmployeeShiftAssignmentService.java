package com.hrm.hrm_api.modules.service.employeeshift;

import com.hrm.hrm_api.modules.entity.EmployeeShiftAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface EmployeeShiftAssignmentService {
    Page<EmployeeShiftAssignment> findAll(Pageable pageable);
    EmployeeShiftAssignment findById(UUID id);
    EmployeeShiftAssignment create(EmployeeShiftAssignment entity);
    EmployeeShiftAssignment update(UUID id, EmployeeShiftAssignment entity);
    void delete(UUID id);
}