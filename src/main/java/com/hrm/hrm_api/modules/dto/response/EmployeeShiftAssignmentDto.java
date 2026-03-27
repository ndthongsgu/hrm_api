package com.hrm.hrm_api.modules.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public class EmployeeShiftAssignmentDto {

    private UUID id;

    private UUID employeeId;

    private String employeeCode;

    private String employeeName;

    private UUID shiftId;

    private String shiftName;

    private LocalDate startDate;

    private LocalDate endDate;

    public EmployeeShiftAssignmentDto() {
    }

    public EmployeeShiftAssignmentDto(
            UUID id,
            UUID employeeId,
            String employeeCode,
            String employeeName,
            UUID shiftId,
            String shiftName,
            LocalDate startDate,
            LocalDate endDate) {
        this.id = id;
        this.employeeId = employeeId;
        this.employeeCode = employeeCode;
        this.employeeName = employeeName;
        this.shiftId = shiftId;
        this.shiftName = shiftName;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
