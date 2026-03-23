package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.modules.dto.base.BaseResponse;
import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.base.SelectOptionDto;
import com.hrm.hrm_api.modules.dto.request.EmployeeSearchRequest;
import com.hrm.hrm_api.modules.dto.response.KnownFaceUploadResponseDto;
import com.hrm.hrm_api.modules.entity.Employee;
import com.hrm.hrm_api.modules.service.employee.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/employees")
@Slf4j
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<BaseResponse<PaginationResponse<Employee>>> search(@RequestBody(required = false) EmployeeSearchRequest request) {
        PaginationResponse<Employee> response = employeeService.search(request);
        return ResponseEntity.ok(BaseResponse.ok(response));
    }

    @GetMapping("/select-options")
    public ResponseEntity<BaseResponse<List<SelectOptionDto>>> getSelectOptions() {
        List<SelectOptionDto> options = employeeService.getSelectOptions();
        return ResponseEntity.ok(BaseResponse.ok(options));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<Employee>> getById(@PathVariable java.util.UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(employeeService.findById(id)));
    }

    @PostMapping("/create")
    public ResponseEntity<BaseResponse<Employee>> create(@Valid @RequestBody Employee payload) {
        return ResponseEntity.ok(BaseResponse.ok(employeeService.create(payload)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<Employee>> update(@PathVariable java.util.UUID id, @Valid @RequestBody Employee payload) {
        return ResponseEntity.ok(BaseResponse.ok(employeeService.update(id, payload)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable java.util.UUID id) {
        employeeService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok(null));
    }

    /**
     * Upload known face images for an employee
     * This endpoint accepts multiple image files and forwards them to Python API for face recognition setup
     *
     * @param employeeId Employee UUID
     * @param files List of image files to upload (jpg, jpeg, png, gif, bmp)
     * @return Upload response containing status and details for each file
     */
    @PostMapping("/{employeeId}/known-faces/upload")
    public ResponseEntity<BaseResponse<KnownFaceUploadResponseDto>> uploadKnownFaces(
            @PathVariable UUID employeeId,
            @RequestParam(value = "files", required = false) List<MultipartFile> files
    ) {
        log.info("Received known face upload request for employee: {} with {} files", 
                employeeId, files != null ? files.size() : 0);

        try {
            if (files == null || files.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(BaseResponse.error("At least one file is required"));
            }

            // Convert UUID to String for Python API
            String employeeIdStr = employeeId.toString();

            // Upload to Python API
            KnownFaceUploadResponseDto response = employeeService.uploadKnownFaces(employeeIdStr, files);

            log.info("Successfully uploaded known faces for employee: {} - Status: {}", 
                    employeeId, response.getStatus());

            return ResponseEntity.ok(BaseResponse.ok(response));

        } catch (IllegalArgumentException e) {
            log.warn("Invalid argument for employee {}: {}", employeeId, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(BaseResponse.error(e.getMessage()));
        } catch (RuntimeException e) {
            log.error("Error uploading known faces for employee {}: {}", employeeId, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(BaseResponse.error("Failed to upload known faces: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error uploading known faces for employee {}: {}", employeeId, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(BaseResponse.error("An unexpected error occurred"));
        }
    }
}