package com.hrm.hrm_api.modules.dto.position;

import lombok.Data;

@Data
public class PositionFilter {
    private String name;
    private Boolean isDeleted;
}