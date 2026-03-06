package com.hrm.hrm_api.modules.dto.employee;

import lombok.Data;

@Data
public class EmployeeFilter {
    private String fullName;
    private String employeeCode;
    private Boolean isDeleted;
}