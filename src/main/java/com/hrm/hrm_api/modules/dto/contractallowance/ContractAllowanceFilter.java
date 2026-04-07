package com.hrm.hrm_api.modules.dto.contractallowance;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContractAllowanceFilter {
    private UUID contractId;
    private UUID allowanceTypeId;
    private UUID employeeId;
    private Boolean isDeleted;
}
