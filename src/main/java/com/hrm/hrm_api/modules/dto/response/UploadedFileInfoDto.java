package com.hrm.hrm_api.modules.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadedFileInfoDto {
    private String filename;
    private String status;
    private String message;
}
