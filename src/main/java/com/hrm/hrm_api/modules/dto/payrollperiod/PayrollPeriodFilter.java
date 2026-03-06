package com.hrm.hrm_api.modules.dto.payrollperiod;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PayrollPeriodFilter {
    private String name;
    private Boolean isLocked;
    private Boolean isDeleted;
    private LocalDate startDate;
    private LocalDate endDate;
}