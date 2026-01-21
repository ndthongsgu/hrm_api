package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.InsuranceConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InsuranceConfigRepository extends JpaRepository<InsuranceConfig, UUID> {
}