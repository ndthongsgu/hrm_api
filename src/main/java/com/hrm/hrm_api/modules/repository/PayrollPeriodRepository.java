package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.PayrollPeriod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PayrollPeriodRepository extends JpaRepository<PayrollPeriod, UUID> {
}