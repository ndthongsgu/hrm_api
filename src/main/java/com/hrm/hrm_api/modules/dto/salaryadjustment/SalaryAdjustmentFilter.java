package com.hrm.hrm_api.modules.dto.salaryadjustment;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class SalaryAdjustmentFilter {
    private UUID payrollPeriodId;
    private UUID employeeId;
    private BigDecimal amount;
    private String type;
    private String reason;
    private Boolean isTaxable;
    private Boolean isNetOnly;
    private Boolean isDeleted;
}