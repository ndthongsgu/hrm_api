package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}