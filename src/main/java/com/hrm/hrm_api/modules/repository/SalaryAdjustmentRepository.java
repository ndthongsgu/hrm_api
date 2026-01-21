package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.SalaryAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaryAdjustmentRepository extends JpaRepository<SalaryAdjustment, Long> {
}