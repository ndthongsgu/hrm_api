package com.hrm.hrm_api.modules.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class ContractRequestDto {
    @NotNull
    private UUID employeeId;

    @NotBlank
    private String contractNumber;

    @NotNull
    private BigDecimal baseSalary;

    @NotNull
    private BigDecimal actualSalary;

    private UUID positionId;

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;

    @NotBlank
    private String status;

    private Boolean isNet = false;
}
