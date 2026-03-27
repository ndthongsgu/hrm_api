package com.hrm.hrm_api.modules.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttendanceCheckInResponse {
    
    private boolean success; // true if check-in successful, false otherwise
    private UUID attendanceId;
    private String employeeId;
    private String employeeName;
    private LocalDateTime checkInTime;
    private String status; // OK, LATE, EARLY, ABSENT, LEAVE
    private Integer lateMinutes;
    private Integer earlyMinutes;
    private Double workingHours;
    
    private boolean faceVerified;
    private String faceVerificationMessage;
    
    private boolean locationVerified;
    private Double distanceToOffice; // in meters
    private String officeLocationName;
    
    private String checkInResult; // SUCCESS, FACE_VERIFICATION_FAILED, LOCATION_NOT_VERIFIED, OTHER_ERROR
    private String message;
}
