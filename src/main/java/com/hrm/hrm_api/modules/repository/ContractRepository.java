package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContractRepository extends JpaRepository<Contract, UUID> {
	List<Contract> findByEmployeeIdAndStatusAndStartDateLessThanEqualOrderByStartDateDesc(
			UUID employeeId,
			String status,
			LocalDate date
	);

    List<Contract> findByEmployeeIdAndStartDateLessThanEqualOrderByStartDateDesc(
            UUID employeeId,
            LocalDate date
    );
    
    @Query("SELECT c FROM Contract c WHERE c.employee.id = :employeeId AND c.status = :status")
    Optional<Contract> findByEmployeeAndStatus(
        @Param("employeeId") UUID employeeId,
        @Param("status") String status
    );
}