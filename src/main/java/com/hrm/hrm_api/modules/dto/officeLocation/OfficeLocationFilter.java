package com.hrm.hrm_api.modules.dto.officeLocation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hrm.hrm_api.modules.entity.OfficeLocation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OfficeLocationFilter {
    private String name;
    private String address;
    private OfficeLocation.Status status;
    private Boolean isDeleted;
}
