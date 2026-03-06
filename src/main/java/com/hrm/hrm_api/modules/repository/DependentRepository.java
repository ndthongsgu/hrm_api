package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.Dependent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DependentRepository extends JpaRepository<Dependent, UUID> {
	long countByEmployeeIdAndIsQualifiedTrue(UUID employeeId);
}