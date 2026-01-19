package com.hrm.hrm_api.modules.service;

import com.hrm.hrm_api.modules.entity.LaborContract;
import com.hrm.hrm_api.modules.repository.LaborContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LaborContractService {

    @Autowired
    private LaborContractRepository laborContractRepository;

    public List<LaborContract> getAllContracts() {
        return laborContractRepository.findAll();
    }

    public Optional<LaborContract> getContractById(Long id) {
        return laborContractRepository.findById(id);
    }

    public LaborContract saveContract(LaborContract contract) {
        return laborContractRepository.save(contract);
    }

    public void deleteContract(Long id) {
        laborContractRepository.deleteById(id);
    }
}
