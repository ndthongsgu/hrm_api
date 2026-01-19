package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.modules.entity.SalaryStructure;
import com.hrm.hrm_api.modules.service.SalaryStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salary-structures")
public class SalaryStructureController {

    @Autowired
    private SalaryStructureService salaryStructureService;

    @GetMapping
    public List<SalaryStructure> getAllSalaryStructures() {
        return salaryStructureService.getAllSalaryStructures();
    }

    @GetMapping("/{id}")
    public SalaryStructure getSalaryStructureById(@PathVariable Long id) {
        return salaryStructureService.getSalaryStructureById(id).orElse(null);
    }

    @PostMapping
    public SalaryStructure createSalaryStructure(@RequestBody SalaryStructure salaryStructure) {
        return salaryStructureService.saveSalaryStructure(salaryStructure);
    }

    @PutMapping("/{id}")
    public SalaryStructure updateSalaryStructure(@PathVariable Long id, @RequestBody SalaryStructure salaryStructure) {
        salaryStructure.setId(id);
        return salaryStructureService.saveSalaryStructure(salaryStructure);
    }

    @DeleteMapping("/{id}")
    public void deleteSalaryStructure(@PathVariable Long id) {
        salaryStructureService.deleteSalaryStructure(id);
    }
}
