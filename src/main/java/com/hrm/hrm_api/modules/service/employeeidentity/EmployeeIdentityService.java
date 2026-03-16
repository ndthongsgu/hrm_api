package com.hrm.hrm_api.modules.service.employeeidentity;

import com.hrm.hrm_api.modules.entity.EmployeeIdentity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface EmployeeIdentityService {
    Page<EmployeeIdentity> findAll(Pageable pageable);
    EmployeeIdentity findById(UUID id);
    EmployeeIdentity create(EmployeeIdentity entity);
    EmployeeIdentity update(UUID id, EmployeeIdentity entity);
    void delete(UUID id);
}