package com.hrm.hrm_api.modules.dto.payrollperiod;

import lombok.Data;

import java.time.LocalDate;

import com.hrm.hrm_api.modules.entity.PayrollPeriod;

@Data
public class PayrollPeriodFilter {
    private String name;
    private PayrollPeriod.Status status; // APPROVED, REJECTED, PENDING, LOCKED;
    private Boolean isDeleted;
    private LocalDate startDate;
    private LocalDate endDate;
}