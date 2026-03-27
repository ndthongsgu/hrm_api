package com.hrm.hrm_api.modules.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Component
public class FaceVerificationClient {
    
    private final RestTemplate restTemplate;
    
    @Value("${python-api.base-url:http://localhost:8000}")
    private String faceVerificationApiUrl;
    
    public FaceVerificationClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    /**
     * Gọi API face verification để xác minh khuôn mặt của nhân viên
     * 
     * @param employeeId ID của nhân viên
     * @param imageFile File ảnh cần verify
     * @return VerifyFaceResponse với kết quả xác minh
     */
    public VerifyFaceResponse verifyFace(String employeeId, MultipartFile imageFile) {
        try {
            String url = faceVerificationApiUrl + "/api/verify";
            
            // Create multipart request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("employee_id", employeeId);
            body.add("file", new MultipartFileResource(imageFile));
            
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            
            log.info("Calling face verification API for employee: {}", employeeId);
            ResponseEntity<VerifyFaceResponse> response = restTemplate.postForEntity(
                url,
                requestEntity,
                VerifyFaceResponse.class
            );
            
            log.info("Face verification response: {}", response.getBody());
            return response.getBody();
            
        } catch (Exception e) {
            log.error("Error calling face verification API for employee: {}", employeeId, e);
            return VerifyFaceResponse.builder()
                .status("failed")
                .message("Error connecting to face verification service: " + e.getMessage())
                .build();
        }
    }
    
    /**
     * DTO for face verification response
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @lombok.Builder
    public static class VerifyFaceResponse {
        private String status; // success, failed
        private String employeeId;
        private String message;
        private Double confidence; // Confidence score (0-1) if available
    }
    
    /**
     * Helper class to wrap MultipartFile for RestTemplate
     */
    private static class MultipartFileResource extends org.springframework.core.io.ByteArrayResource {
        private final String filename;
        
        public MultipartFileResource(MultipartFile file) throws IOException {
            super(file.getBytes());
            this.filename = file.getOriginalFilename();
        }
        
        @Override
        public String getFilename() {
            return filename;
        }
    }
}
