package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.DailyAttendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DailyAttendanceRepository extends JpaRepository<DailyAttendance, UUID> {
}