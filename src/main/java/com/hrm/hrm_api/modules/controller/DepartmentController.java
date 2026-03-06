package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.modules.dto.base.BaseResponse;
import com.hrm.hrm_api.modules.dto.base.PageResponse;
import com.hrm.hrm_api.modules.dto.request.DepartmentSearchRequest;
import com.hrm.hrm_api.modules.entity.Department;
import com.hrm.hrm_api.modules.service.department.DepartmentService;
import com.hrm.hrm_api.modules.util.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @PostMapping
    public ResponseEntity<BaseResponse<PageResponse<Department>>> search(
            @RequestBody(required = false) DepartmentSearchRequest request) {
        if (request == null) {
            request = new DepartmentSearchRequest();
        }
        Page<Department> page = departmentService.findByFilters(
                org.springframework.data.domain.PageRequest.of(
                        request.getSkip() / request.getTake(),
                        request.getTake()
                ),
                request.getWhere()
        );
        return ResponseEntity.ok(BaseResponse.ok(PaginationUtils.toPageResponse(page)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<Department>> getById(@PathVariable java.util.UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(departmentService.findById(id)));
    }

    @PostMapping("/create")
    public ResponseEntity<BaseResponse<Department>> create(@Valid @RequestBody Department payload) {
        return ResponseEntity.ok(BaseResponse.ok(departmentService.create(payload)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<Department>> update(@PathVariable java.util.UUID id, @Valid @RequestBody Department payload) {
        return ResponseEntity.ok(BaseResponse.ok(departmentService.update(id, payload)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable java.util.UUID id) {
        departmentService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok(null));
    }
}