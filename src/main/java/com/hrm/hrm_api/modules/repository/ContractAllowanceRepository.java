package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.ContractAllowance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ContractAllowanceRepository extends JpaRepository<ContractAllowance, UUID> {
	List<ContractAllowance> findByContractId(UUID contractId);
}