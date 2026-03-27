package com.hrm.hrm_api.modules.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceCheckInRequest {
    
    @NotBlank(message = "Employee ID is required")
    private String employeeId;
    
    @NotNull(message = "Image file is required")
    private MultipartFile imageFile;
    
    @NotNull(message = "Latitude is required")
    private Double latitude;
    
    @NotNull(message = "Longitude is required")
    private Double longitude;
    
    private String deviceId; // Optional, for tracking device information
    private String notes; // Optional, additional notes
}
