package com.hrm.hrm_api.modules.service;

import com.hrm.hrm_api.modules.entity.SalaryStructure;
import com.hrm.hrm_api.modules.repository.SalaryStructureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SalaryStructureService {

    @Autowired
    private SalaryStructureRepository salaryStructureRepository;

    public List<SalaryStructure> getAllSalaryStructures() {
        return salaryStructureRepository.findAll();
    }

    public Optional<SalaryStructure> getSalaryStructureById(Long id) {
        return salaryStructureRepository.findById(id);
    }

    public SalaryStructure saveSalaryStructure(SalaryStructure salaryStructure) {
        return salaryStructureRepository.save(salaryStructure);
    }

    public void deleteSalaryStructure(Long id) {
        salaryStructureRepository.deleteById(id);
    }
}
