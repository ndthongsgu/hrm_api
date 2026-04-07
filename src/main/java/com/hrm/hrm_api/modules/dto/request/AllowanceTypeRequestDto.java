package com.hrm.hrm_api.modules.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AllowanceTypeRequestDto {
    @NotBlank
    private String name;

    private Boolean isTaxable = true;

    private Boolean isInsuranceBased = true;
}
