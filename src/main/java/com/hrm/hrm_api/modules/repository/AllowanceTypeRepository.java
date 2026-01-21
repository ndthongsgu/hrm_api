package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.AllowanceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AllowanceTypeRepository extends JpaRepository<AllowanceType, UUID> {
}