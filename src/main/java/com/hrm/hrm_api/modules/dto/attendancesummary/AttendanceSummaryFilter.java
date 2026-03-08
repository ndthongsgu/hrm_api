package com.hrm.hrm_api.modules.dto.attendancesummary;

import lombok.Data;

import java.util.UUID;

@Data
public class AttendanceSummaryFilter {
    private UUID payrollPeriodId;
    private UUID employeeId;
    private Boolean isApproved;
    private Boolean isDeleted;
}
