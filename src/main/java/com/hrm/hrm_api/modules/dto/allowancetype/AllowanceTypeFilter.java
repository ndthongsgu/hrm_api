package com.hrm.hrm_api.modules.dto.allowancetype;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AllowanceTypeFilter {
    private String name;
    private Boolean isTaxable;
    private Boolean isInsuranceBased;
    private Boolean isDeleted;
}
