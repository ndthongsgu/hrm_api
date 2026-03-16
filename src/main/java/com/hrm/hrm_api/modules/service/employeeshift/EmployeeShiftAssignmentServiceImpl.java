package com.hrm.hrm_api.modules.service.employeeshift;

import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import com.hrm.hrm_api.modules.entity.EmployeeShiftAssignment;
import com.hrm.hrm_api.modules.repository.EmployeeShiftAssignmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class EmployeeShiftAssignmentServiceImpl implements EmployeeShiftAssignmentService {

    private final EmployeeShiftAssignmentRepository repository;

    public EmployeeShiftAssignmentServiceImpl(EmployeeShiftAssignmentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<EmployeeShiftAssignment> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public EmployeeShiftAssignment findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(EmployeeShiftAssignment.class, id));
    }

    @Override
    @Transactional
    public EmployeeShiftAssignment create(EmployeeShiftAssignment entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public EmployeeShiftAssignment update(UUID id, EmployeeShiftAssignment entity) {
        findById(id);
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(EmployeeShiftAssignment.class, id);
        }
        repository.deleteById(id);
    }
}