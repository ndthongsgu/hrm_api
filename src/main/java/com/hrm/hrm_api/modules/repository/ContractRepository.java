package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ContractRepository extends JpaRepository<Contract, UUID> {
}