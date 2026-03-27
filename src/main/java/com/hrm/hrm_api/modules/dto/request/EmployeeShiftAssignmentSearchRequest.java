package com.hrm.hrm_api.modules.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hrm.hrm_api.modules.dto.workshift.EmployeeShiftAssignmentFilter;

public class EmployeeShiftAssignmentSearchRequest {

    @JsonProperty("where")
    private EmployeeShiftAssignmentFilter where;

    public EmployeeShiftAssignmentSearchRequest() {
    }

    public EmployeeShiftAssignmentSearchRequest(EmployeeShiftAssignmentFilter where) {
        this.where = where;
    }

    public EmployeeShiftAssignmentFilter getWhere() {
        return where;
    }

    public void setWhere(EmployeeShiftAssignmentFilter where) {
        this.where = where;
    }
}
