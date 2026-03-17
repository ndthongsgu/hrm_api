package com.hrm.hrm_api.modules.dto.attendancesummary;

import lombok.Data;

import java.util.UUID;

import com.hrm.hrm_api.modules.entity.AttendanceSummary;

@Data
public class AttendanceSummaryFilter {
    private UUID payrollPeriodId;
    private UUID employeeId;
    private AttendanceSummary.Status status; // APPROVED, PENDING, LOCKED;
    private Boolean isDeleted;
}
