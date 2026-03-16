package com.hrm.hrm_api.modules.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AttendanceSummaryCreateRequest {
    @NotNull
    private UUID payrollPeriodId;

    @NotNull
    private UUID employeeId;

    private Float actualWorkingDays;
    private Float paidLeaveDays;
    private Float unpaidLeaveDays;
    private Float overtimeHours;
    private Boolean isApproved = false;
}
