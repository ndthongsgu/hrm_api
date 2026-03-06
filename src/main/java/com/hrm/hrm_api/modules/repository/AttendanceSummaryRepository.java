package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.AttendanceSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AttendanceSummaryRepository extends JpaRepository<AttendanceSummary, UUID> {
	List<AttendanceSummary> findByPayrollPeriodId(UUID payrollPeriodId);
}