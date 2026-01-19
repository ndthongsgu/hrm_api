package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.modules.entity.LaborContract;
import com.hrm.hrm_api.modules.service.LaborContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contracts")
public class LaborContractController {

    @Autowired
    private LaborContractService laborContractService;

    @GetMapping
    public List<LaborContract> getAllContracts() {
        return laborContractService.getAllContracts();
    }

    @GetMapping("/{id}")
    public LaborContract getContractById(@PathVariable Long id) {
        return laborContractService.getContractById(id).orElse(null);
    }

    @PostMapping
    public LaborContract createContract(@RequestBody LaborContract contract) {
        return laborContractService.saveContract(contract);
    }

    @PutMapping("/{id}")
    public LaborContract updateContract(@PathVariable Long id, @RequestBody LaborContract contract) {
        contract.setId(id);
        return laborContractService.saveContract(contract);
    }

    @DeleteMapping("/{id}")
    public void deleteContract(@PathVariable Long id) {
        laborContractService.deleteContract(id);
    }
}
