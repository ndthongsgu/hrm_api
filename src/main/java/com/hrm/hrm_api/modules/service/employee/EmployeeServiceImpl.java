package com.hrm.hrm_api.modules.service.employee;

import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.base.SelectOptionDto;
import com.hrm.hrm_api.modules.dto.request.EmployeeSearchRequest;
import com.hrm.hrm_api.modules.entity.Employee;
import com.hrm.hrm_api.modules.repository.EmployeeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository repository) {
        this.employeeRepository = repository;
    }

    @Override
    public Page<Employee> findAll(Pageable pageable) {
        return employeeRepository.findAll(pageable);
    }

    @Override
    public Employee findById(UUID id) {
        return employeeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Employee.class, id));
    }

    @Override
    @Transactional
    public Employee create(Employee entity) {
        return employeeRepository.save(entity);
    }

    @Override
    @Transactional
    public Employee update(UUID id, Employee entity) {
        Employee existing = findById(id);
        existing.setFullName(entity.getFullName());
        existing.setEmployeeCode(entity.getEmployeeCode());
        existing.setEmail(entity.getEmail());
        existing.setPhone(entity.getPhone());
        existing.setDepartment(entity.getDepartment());
        existing.setPosition(entity.getPosition());
        existing.setTaxCode(entity.getTaxCode());
        existing.setBankAccount(entity.getBankAccount());
        existing.setBankName(entity.getBankName());
        existing.setStatus(entity.getStatus());
        existing.setOfficeLocation(entity.getOfficeLocation());

        if (entity.getIsDeleted() != null) {
            existing.setIsDeleted(entity.getIsDeleted());
        }

        return employeeRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException(Employee.class, id);
        }
        employeeRepository.deleteById(id);
    }

    @Override
    public PaginationResponse<Employee> search(EmployeeSearchRequest request) {
        if (request == null) {
            request = new EmployeeSearchRequest();
            request.setSkip(0);
            request.setTake(20);
        }

        int skip = request.getSkip() != null ? request.getSkip() : 0;
        int take = request.getTake() != null && request.getTake() > 0 ? request.getTake() : 20;

        if (skip < 0) skip = 0;
        if (take <= 0) take = 20;
        if (take > 100) take = 100;

        int pageNumber = skip / take;
        int pageSize = take;

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "created_at"));

        var filter = request.getWhere();

        Page<Employee> employees = employeeRepository.findByFilters(
            filter != null ? filter.getFullName() : null,
            filter != null ? filter.getEmployeeCode() : null,
            filter != null ? filter.getIsDeleted() : null,
            pageable
        );

        return PaginationResponse.of(
            employees.getContent(),
            employees.getTotalElements(),
            pageNumber,
            pageSize
        );
    }

    @Override
    public List<SelectOptionDto> getSelectOptions() {
        List<Employee> employees = employeeRepository.findAllByIsDeletedFalseOrderByCreatedAtDesc();
        return employees.stream()
                .map(employee -> new SelectOptionDto(employee.getId(), employee.getEmployeeCode() + " - " + employee.getFullName()))
                .collect(Collectors.toList());
    }
}
