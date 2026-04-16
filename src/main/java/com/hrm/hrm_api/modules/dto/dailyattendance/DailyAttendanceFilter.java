package com.hrm.hrm_api.modules.dto.dailyattendance;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

import com.hrm.hrm_api.modules.entity.DailyAttendance;

@Data
public class DailyAttendanceFilter {
    private UUID employeeId;
    private UUID shiftId;
    private LocalDate date;
    private LocalDate startDate;
    private LocalDate endDate;
    private DailyAttendance.Status status;
    private Boolean isDeleted;
}