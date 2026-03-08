package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.common.constants.PermissionDefinition;
import com.hrm.hrm_api.config.annotations.RequirePermission;
import com.hrm.hrm_api.modules.dto.base.BaseResponse;
import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.base.SelectOptionDto;
import com.hrm.hrm_api.modules.dto.request.EmployeeSearchRequest;
import com.hrm.hrm_api.modules.entity.Employee;
import com.hrm.hrm_api.modules.service.employee.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/employees")
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
}