package com.hrm.hrm_api.modules.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;
import com.hrm.hrm_api.modules.entity.SalaryAdjustment;

@Data
public class SalaryAdjustmentCreateRequest {
    @NotNull
    private UUID payrollPeriodId;

    @NotNull
    private UUID employeeId;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private SalaryAdjustment.Type type;

    private String reason;

    private Boolean isTaxable = true;

    private Boolean isNetOnly = false;
}
