package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.modules.dto.base.BaseResponse;
import com.hrm.hrm_api.modules.dto.base.PageResponse;
import com.hrm.hrm_api.modules.entity.EmployeeIdentity;
import com.hrm.hrm_api.modules.service.employeeidentity.EmployeeIdentityService;
import com.hrm.hrm_api.modules.util.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/employee-identities")
public class EmployeeIdentityController {

    private final EmployeeIdentityService service;

    public EmployeeIdentityController(EmployeeIdentityService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<EmployeeIdentity>>> list(@PageableDefault(size = 20) Pageable pageable) {
        Page<EmployeeIdentity> page = service.findAll(pageable);
        return ResponseEntity.ok(BaseResponse.ok(PaginationUtils.toPageResponse(page)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<EmployeeIdentity>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(BaseResponse.ok(service.findById(id)));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<EmployeeIdentity>> create(@Valid @RequestBody EmployeeIdentity payload) {
        return ResponseEntity.ok(BaseResponse.ok(service.create(payload)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<EmployeeIdentity>> update(@PathVariable Long id, @Valid @RequestBody EmployeeIdentity payload) {
        return ResponseEntity.ok(BaseResponse.ok(service.update(id, payload)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(BaseResponse.ok(null));
    }
}