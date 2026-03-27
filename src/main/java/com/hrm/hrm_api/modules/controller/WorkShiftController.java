package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.modules.dto.base.BaseResponse;
import com.hrm.hrm_api.modules.dto.base.PageResponse;
import com.hrm.hrm_api.modules.dto.base.SelectOptionDto;
import com.hrm.hrm_api.modules.dto.request.WorkShiftSearchRequest;
import com.hrm.hrm_api.modules.entity.WorkShift;
import com.hrm.hrm_api.modules.service.workshift.WorkShiftService;
import com.hrm.hrm_api.modules.util.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/work-shifts")
public class WorkShiftController {

    private final WorkShiftService service;

    public WorkShiftController(WorkShiftService service) {
        this.service = service;
    }

    /**
     * Get all work shifts with pagination
     */
    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<WorkShift>>> list(@PageableDefault(size = 20) Pageable pageable) {
        Page<WorkShift> page = service.findAll(pageable);
        return ResponseEntity.ok(BaseResponse.ok(PaginationUtils.toPageResponse(page)));
    }

    /**
     * Search work shifts with filtering and pagination
     */
    @PostMapping("/search")
    public ResponseEntity<BaseResponse<PageResponse<WorkShift>>> search(
            @RequestBody(required = false) WorkShiftSearchRequest request,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        var paginationResponse = service.search(request, pageable);
        PageResponse<WorkShift> response = PageResponse.of(
                paginationResponse.getContent(),
                paginationResponse.getTotalElements(),
                paginationResponse.getPage(),
                paginationResponse.getSize());
        return ResponseEntity.ok(BaseResponse.ok(response));
    }

    /**
     * Get all work shifts as select options
     */
    @GetMapping("/select-options")
    public ResponseEntity<BaseResponse<List<SelectOptionDto>>> getSelectOptions() {
        List<SelectOptionDto> options = service.getSelectOptions();
        return ResponseEntity.ok(BaseResponse.ok(options));
    }

    /**
     * Get work shift by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<WorkShift>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(service.findById(id)));
    }

    /**
     * Create a new work shift
     */
    @PostMapping
    public ResponseEntity<BaseResponse<WorkShift>> create(@Valid @RequestBody WorkShift payload) {
        return ResponseEntity.ok(BaseResponse.ok(service.create(payload)));
    }

    /**
     * Update an existing work shift
     */
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<WorkShift>> update(@PathVariable UUID id, @Valid @RequestBody WorkShift payload) {
        return ResponseEntity.ok(BaseResponse.ok(service.update(id, payload)));
    }

    /**
     * Delete a work shift
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(BaseResponse.ok(null));
    }
}