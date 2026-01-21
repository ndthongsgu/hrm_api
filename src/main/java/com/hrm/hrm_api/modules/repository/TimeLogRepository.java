package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.TimeLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeLogRepository extends JpaRepository<TimeLog, Long> {
}