package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.LaborContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LaborContractRepository extends JpaRepository<LaborContract, Long> {
}
