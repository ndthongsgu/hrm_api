package com.hrm.hrm_api.modules.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetKnownFacesResponseDto {
    private String status;
    
    @JsonProperty("employee_id")
    private String employeeId;
    
    @JsonProperty("total_images")
    private Integer totalImages;
    
    private List<KnownFaceImageDto> images;
}
