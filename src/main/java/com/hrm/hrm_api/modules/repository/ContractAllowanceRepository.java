package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.ContractAllowance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ContractAllowanceRepository extends JpaRepository<ContractAllowance, UUID> {
    
    @Query("SELECT ca FROM ContractAllowance ca WHERE ca.contract.id = :contractId")
    List<ContractAllowance> findByContractId(@Param("contractId") UUID contractId);
    
    @Query("SELECT ca FROM ContractAllowance ca WHERE ca.contract.id = :contractId AND ca.isDeleted = :isDeleted")
    List<ContractAllowance> findByContractIdAndIsDeleted(@Param("contractId") UUID contractId, @Param("isDeleted") Boolean isDeleted);
}