package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.modules.dto.base.BaseResponse;
import com.hrm.hrm_api.modules.dto.base.PageResponse;
import com.hrm.hrm_api.modules.entity.Employee;
import com.hrm.hrm_api.modules.service.employee.EmployeeService;
import com.hrm.hrm_api.modules.util.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<Employee>>> list(@PageableDefault(size = 20) Pageable pageable) {
        Page<Employee> page = employeeService.findAll(pageable);
        return ResponseEntity.ok(BaseResponse.ok(PaginationUtils.toPageResponse(page)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<Employee>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(BaseResponse.ok(employeeService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<Employee>> create(@Valid @RequestBody Employee payload) {
        return ResponseEntity.ok(BaseResponse.ok(employeeService.create(payload)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<Employee>> update(@PathVariable Long id, @Valid @RequestBody Employee payload) {
        return ResponseEntity.ok(BaseResponse.ok(employeeService.update(id, payload)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok(null));
    }
}