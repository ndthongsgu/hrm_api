package com.hrm.hrm_api.modules.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActionLogSearchRequest {
    private Integer skip = 0;
    private Integer take = 20;
    private ActionLogFilterRequest where;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ActionLogFilterRequest {
        private String functionType;
        private String functionId;
        private String type;
        private String createdBy;
    }
}
