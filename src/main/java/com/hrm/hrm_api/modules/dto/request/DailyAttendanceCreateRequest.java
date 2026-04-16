package com.hrm.hrm_api.modules.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class DailyAttendanceCreateRequest {
    @NotNull(message = "Employee ID is required")
    private UUID employeeId;

    @NotNull(message = "Date is required")
    private LocalDate date;

    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    @NotNull(message = "Status is required")
    private String status; // OK, LATE, ABSENT, LEAVE, EARLY
}