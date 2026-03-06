package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ContractRepository extends JpaRepository<Contract, UUID> {
	List<Contract> findByEmployeeIdAndStatusAndStartDateLessThanEqualOrderByStartDateDesc(
			UUID employeeId,
			String status,
			LocalDate date
	);
}