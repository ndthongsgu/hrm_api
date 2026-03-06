package com.hrm.hrm_api.modules.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleSearchRequest {
    private Integer skip = 0;
    private Integer take = 20;
    private RoleFilterRequest where;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RoleFilterRequest {
        private String code;
        private String name;
        private Boolean isActive;
    }
}
