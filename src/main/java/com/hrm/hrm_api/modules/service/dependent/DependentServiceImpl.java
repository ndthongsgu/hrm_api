package com.hrm.hrm_api.modules.service.dependent;

import com.hrm.hrm_api.common.exception.BadRequestException;
import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import com.hrm.hrm_api.modules.entity.Dependent;
import com.hrm.hrm_api.modules.entity.Employee;
import com.hrm.hrm_api.modules.repository.DependentRepository;
import com.hrm.hrm_api.modules.repository.EmployeeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DependentServiceImpl implements DependentService {

    private final DependentRepository repository;
    private final EmployeeRepository employeeRepository;

    public DependentServiceImpl(DependentRepository repository, EmployeeRepository employeeRepository) {
        this.repository = repository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Page<Dependent> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Page<Dependent> findByEmployeeId(UUID employeeId, String fullName, String relationship, Pageable pageable) {
        return repository.findByEmployeeIdAndFilters(employeeId, fullName, relationship, pageable);
    }

    @Override
    public Dependent findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Dependent.class, id));
    }

    @Override
    @Transactional
    public Dependent create(Dependent entity) {
        entity.setEmployee(resolveEmployee(entity));
        return repository.save(entity);
    }

    @Override
    @Transactional
    public Dependent update(UUID id, Dependent entity) {
        Dependent existing = findById(id);

        if (entity.getEmployee() != null && entity.getEmployee().getId() != null) {
            existing.setEmployee(resolveEmployee(entity));
        }

        existing.setFullName(entity.getFullName());
        existing.setRelationship(entity.getRelationship());
        existing.setBirthDate(entity.getBirthDate());
        existing.setIsQualified(entity.getIsQualified());

        return repository.save(existing);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(Dependent.class, id);
        }
        repository.deleteById(id);
    }

    private Employee resolveEmployee(Dependent entity) {
        if (entity.getEmployee() == null || entity.getEmployee().getId() == null) {
            throw new BadRequestException("employee.id is required");
        }

        UUID employeeId = entity.getEmployee().getId();
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(Employee.class, employeeId));
    }
}
