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
public class KnownFaceUploadResponseDto {
    private String status;
    private String message;
    
    @JsonProperty("employee_id")
    private String employeeId;
    
    @JsonProperty("total_files")
    private Integer totalFiles;
    
    @JsonProperty("successful_uploads")
    private Integer successfulUploads;
    
    @JsonProperty("failed_uploads")
    private Integer failedUploads;
    
    @JsonProperty("files_info")
    private List<UploadedFileInfoDto> filesInfo;
}
