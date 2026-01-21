package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.AttendanceSummary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceSummaryRepository extends JpaRepository<AttendanceSummary, Long> {
}