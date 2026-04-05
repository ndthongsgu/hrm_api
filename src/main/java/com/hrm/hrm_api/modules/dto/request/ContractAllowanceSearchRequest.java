package com.hrm.hrm_api.modules.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hrm.hrm_api.modules.dto.contractallowance.ContractAllowanceFilter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContractAllowanceSearchRequest {
    private Integer skip = 0;
    private Integer take = 20;
    private ContractAllowanceFilter where;
}
