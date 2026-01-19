package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.config.annotations.RequirePermission;
import com.hrm.hrm_api.common.constants.PermissionAction;
import com.hrm.hrm_api.modules.entity.Employee;
import com.hrm.hrm_api.modules.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    @RequirePermission(module = "EMPLOYEE", action = PermissionAction.READ)
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "EMPLOYEE", action = PermissionAction.READ)
    public Employee getEmployeeById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id).orElse(null);
    }

    @PostMapping
    @RequirePermission(module = "EMPLOYEE", action = PermissionAction.CREATE)
    public Employee createEmployee(@RequestBody Employee employee) {
        return employeeService.saveEmployee(employee);
    }

    @PutMapping("/{id}")
    @RequirePermission(module = "EMPLOYEE", action = PermissionAction.UPDATE)
    public Employee updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        employee.setId(id);
        return employeeService.saveEmployee(employee);
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "EMPLOYEE", action = PermissionAction.DELETE)
    public void deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
    }
}
