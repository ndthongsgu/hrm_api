package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.modules.dto.base.BaseResponse;
import com.hrm.hrm_api.modules.dto.base.PageResponse;
import com.hrm.hrm_api.modules.entity.EmployeeShiftAssignment;
import com.hrm.hrm_api.modules.service.employeeshift.EmployeeShiftAssignmentService;
import com.hrm.hrm_api.modules.util.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/shift-assignments")
public class EmployeeShiftAssignmentController {

    private final EmployeeShiftAssignmentService service;

    public EmployeeShiftAssignmentController(EmployeeShiftAssignmentService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<EmployeeShiftAssignment>>> list(@PageableDefault(size = 20) Pageable pageable) {
        Page<EmployeeShiftAssignment> page = service.findAll(pageable);
        return ResponseEntity.ok(BaseResponse.ok(PaginationUtils.toPageResponse(page)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<EmployeeShiftAssignment>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(BaseResponse.ok(service.findById(id)));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<EmployeeShiftAssignment>> create(@Valid @RequestBody EmployeeShiftAssignment payload) {
        return ResponseEntity.ok(BaseResponse.ok(service.create(payload)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<EmployeeShiftAssignment>> update(@PathVariable Long id, @Valid @RequestBody EmployeeShiftAssignment payload) {
        return ResponseEntity.ok(BaseResponse.ok(service.update(id, payload)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(BaseResponse.ok(null));
    }
}