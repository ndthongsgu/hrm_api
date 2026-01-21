package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.EmployeeIdentity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeIdentityRepository extends JpaRepository<EmployeeIdentity, Long> {
}