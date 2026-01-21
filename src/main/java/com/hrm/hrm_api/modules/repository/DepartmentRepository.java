package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DepartmentRepository extends JpaRepository<Department, UUID> {
}