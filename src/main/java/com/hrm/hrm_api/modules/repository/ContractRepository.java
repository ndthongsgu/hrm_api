package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, Long> {
}