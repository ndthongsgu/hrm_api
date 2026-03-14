package com.hrm.hrm_api.modules.dto.salaryadjustment;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

import com.hrm.hrm_api.modules.entity.SalaryAdjustment;

@Data
public class SalaryAdjustmentFilter {
    private UUID payrollPeriodId;
    private UUID employeeId;
    private BigDecimal amount;
    private SalaryAdjustment.Type type;
    private String reason;
    private Boolean isTaxable;
    private Boolean isNetOnly;
    private Boolean isDeleted;
    private SalaryAdjustment.Status status; // APPROVED, REJECTED, PENDING, LOCKED;
}