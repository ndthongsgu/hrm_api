package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.InsuranceConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface InsuranceConfigRepository extends JpaRepository<InsuranceConfig, UUID> {
	Optional<InsuranceConfig> findTopByTypeOrderByCreatedAtDesc(String type);

	Optional<InsuranceConfig> findTopByTypeAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(String type, LocalDate effectiveDate);
}