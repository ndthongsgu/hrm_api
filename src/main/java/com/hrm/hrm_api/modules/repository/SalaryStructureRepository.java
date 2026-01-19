package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.SalaryStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaryStructureRepository extends JpaRepository<SalaryStructure, Long> {
}
