package com.hrm.hrm_api.modules.dto.contract;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContractFilter {
    private String keyword;
    private String contractNumber;
    private UUID employeeId;
    private String status;
    private Boolean isDeleted;
}
