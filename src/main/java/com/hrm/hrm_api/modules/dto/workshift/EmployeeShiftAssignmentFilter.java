package com.hrm.hrm_api.modules.dto.workshift;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.UUID;

public class EmployeeShiftAssignmentFilter {

    @JsonProperty("employee_id")
    private UUID employeeId;

    @JsonProperty("employee_code")
    private String employeeCode;

    @JsonProperty("employee_name")
    private String employeeName;

    @JsonProperty("shift_id")
    private UUID shiftId;

    @JsonProperty("shift_name")
    private String shiftName;

    @JsonProperty("start_date")
    private LocalDate startDate;

    @JsonProperty("end_date")
    private LocalDate endDate;

    public EmployeeShiftAssignmentFilter() {
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(UUID employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public UUID getShiftId() {
        return shiftId;
    }

    public void setShiftId(UUID shiftId) {
        this.shiftId = shiftId;
    }

    public String getShiftName() {
        return shiftName;
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
