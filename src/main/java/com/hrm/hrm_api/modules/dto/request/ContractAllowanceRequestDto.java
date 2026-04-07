package com.hrm.hrm_api.modules.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ContractAllowanceRequestDto {
    @NotNull
    private UUID contractId;

    @NotNull
    private UUID allowanceTypeId;

    @NotNull
    private BigDecimal amount;
}
