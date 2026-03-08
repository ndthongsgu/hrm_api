package com.hrm.hrm_api.modules.service.dailyattendance;

import com.hrm.hrm_api.modules.entity.DailyAttendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface DailyAttendanceService {
    Page<DailyAttendance> findAll(Pageable pageable);
    DailyAttendance findById(UUID id);
    DailyAttendance create(DailyAttendance entity);
    DailyAttendance update(UUID id, DailyAttendance entity);
    void delete(UUID id);
}