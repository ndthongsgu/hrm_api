package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.PayrollPeriod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayrollPeriodRepository extends JpaRepository<PayrollPeriod, Long> {
}