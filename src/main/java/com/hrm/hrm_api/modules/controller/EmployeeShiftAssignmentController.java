package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.modules.dto.base.BaseResponse;
import com.hrm.hrm_api.modules.dto.base.PageResponse;
import com.hrm.hrm_api.modules.dto.base.SelectOptionDto;
import com.hrm.hrm_api.modules.dto.request.EmployeeShiftAssignmentCreateRequest;
import com.hrm.hrm_api.modules.dto.request.EmployeeShiftAssignmentSearchRequest;
import com.hrm.hrm_api.modules.dto.response.EmployeeShiftAssignmentDto;
import com.hrm.hrm_api.modules.entity.EmployeeShiftAssignment;
import com.hrm.hrm_api.modules.service.employeeshift.EmployeeShiftAssignmentService;
import com.hrm.hrm_api.modules.util.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/shift-assignments")
public class EmployeeShiftAssignmentController {

    private final EmployeeShiftAssignmentService service;

    public EmployeeShiftAssignmentController(EmployeeShiftAssignmentService service) {
        this.service = service;
    }

    /**
     * Get all employee shift assignments with pagination
     */
    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<EmployeeShiftAssignment>>> list(@PageableDefault(size = 20) Pageable pageable) {
        Page<EmployeeShiftAssignment> page = service.findAll(pageable);
        return ResponseEntity.ok(BaseResponse.ok(PaginationUtils.toPageResponse(page)));
    }

    /**
     * Search employee shift assignments with filtering and pagination
     */
    @PostMapping("/search")
    public ResponseEntity<BaseResponse<PageResponse<EmployeeShiftAssignmentDto>>> search(
            @RequestBody(required = false) EmployeeShiftAssignmentSearchRequest request,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        var paginationResponse = service.search(request, pageable);
        PageResponse<EmployeeShiftAssignmentDto> response = PageResponse.of(
                paginationResponse.getContent(),
                paginationResponse.getTotalElements(),
                paginationResponse.getPage(),
                paginationResponse.getSize());
        return ResponseEntity.ok(BaseResponse.ok(response));
    }

    /**
     * Get all employee shift assignments as select options
     */
    @GetMapping("/select-options")
    public ResponseEntity<BaseResponse<List<SelectOptionDto>>> getSelectOptions() {
        List<SelectOptionDto> options = service.getSelectOptions();
        return ResponseEntity.ok(BaseResponse.ok(options));
    }

    /**
     * Get employee shift assignment by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<EmployeeShiftAssignment>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(service.findById(id)));
    }

    /**
     * Create a new employee shift assignment
     */
    @PostMapping
    public ResponseEntity<BaseResponse<EmployeeShiftAssignment>> create(@Valid @RequestBody EmployeeShiftAssignmentCreateRequest request) {
        return ResponseEntity.ok(BaseResponse.ok(service.create(request)));
    }

    /**
     * Update an existing employee shift assignment
     */
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<EmployeeShiftAssignment>> update(@PathVariable UUID id, @Valid @RequestBody EmployeeShiftAssignmentCreateRequest request) {
        return ResponseEntity.ok(BaseResponse.ok(service.update(id, request)));
    }

    /**
     * Delete an employee shift assignment
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(BaseResponse.ok(null));
    }
}