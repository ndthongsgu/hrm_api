package com.hrm.hrm_api.modules.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public class EmployeeShiftAssignmentCreateRequest {

    @NotNull(message = "Employee ID is required")
    private UUID employeeId;

    @NotNull(message = "Shift ID is required")
    private UUID shiftId;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate;

    public EmployeeShiftAssignmentCreateRequest() {
    }

    public EmployeeShiftAssignmentCreateRequest(UUID employeeId, UUID shiftId, LocalDate startDate, LocalDate endDate) {
        this.employeeId = employeeId;
        this.shiftId = shiftId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(UUID employeeId) {
        this.employeeId = employeeId;
    }

    public UUID getShiftId() {
        return shiftId;
    }

    public void setShiftId(UUID shiftId) {
        this.shiftId = shiftId;
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
