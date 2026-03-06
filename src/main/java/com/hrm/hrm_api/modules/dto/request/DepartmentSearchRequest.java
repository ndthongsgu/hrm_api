package com.hrm.hrm_api.modules.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DepartmentSearchRequest {
    private Integer skip = 0;
    private Integer take = 20;
    private DepartmentFilterRequest where;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DepartmentFilterRequest {
        private String code;
        private String name;
        private String managerId;
        private String parentDepartmentId;
        private Boolean isActive;
    }
}
