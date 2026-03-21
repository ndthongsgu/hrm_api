package com.hrm.hrm_api.modules.service.employee;

import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.base.SelectOptionDto;
import com.hrm.hrm_api.modules.dto.request.EmployeeSearchRequest;
import com.hrm.hrm_api.modules.entity.Employee;
import com.hrm.hrm_api.modules.entity.Department;
import com.hrm.hrm_api.modules.entity.Position;
import com.hrm.hrm_api.modules.repository.DepartmentRepository;
import com.hrm.hrm_api.modules.repository.EmployeeRepository;
import com.hrm.hrm_api.modules.repository.PositionRepository;
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
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;

    public EmployeeServiceImpl(
            EmployeeRepository repository,
            DepartmentRepository departmentRepository,
            PositionRepository positionRepository
    ) {
        this.employeeRepository = repository;
        this.departmentRepository = departmentRepository;
        this.positionRepository = positionRepository;
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
        Department resolvedDepartment = resolveDepartment(entity);
        if (resolvedDepartment != null) {
            entity.setDepartment(resolvedDepartment);
        }

        Position resolvedPosition = resolvePosition(entity);
        if (resolvedPosition != null) {
            entity.setPosition(resolvedPosition);
        }

        if (entity.getIsDeleted() == null) {
            entity.setIsDeleted(false);
        }
        return employeeRepository.save(entity);
    }

    @Override
    @Transactional
    public Employee update(UUID id, Employee entity) {
        employeeRepository.normalizeNullVersion(id);
        Employee existing = findById(id);

        existing.setFullName(entity.getFullName());
        existing.setEmployeeCode(entity.getEmployeeCode());
        existing.setEmail(entity.getEmail());
        existing.setPhone(entity.getPhone());
        existing.setEmergencyContact(entity.getEmergencyContact());
        existing.setEmergencyPhone(entity.getEmergencyPhone());
        existing.setSocialInsuranceNumber(entity.getSocialInsuranceNumber());
        existing.setHealthInsuranceNumber(entity.getHealthInsuranceNumber());
        existing.setAddress(entity.getAddress());
        existing.setGender(entity.getGender());
        existing.setDateOfBirth(entity.getDateOfBirth());
        existing.setHireDate(entity.getHireDate());
        existing.setNotes(entity.getNotes());

        Department resolvedDepartment = resolveDepartment(entity);
        if (resolvedDepartment != null) {
            existing.setDepartment(resolvedDepartment);
        } else if (entity.getDepartment() != null) {
            existing.setDepartment(entity.getDepartment());
        }

        Position resolvedPosition = resolvePosition(entity);
        if (resolvedPosition != null) {
            existing.setPosition(resolvedPosition);
        } else if (entity.getPosition() != null) {
            existing.setPosition(entity.getPosition());
        }

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

    private Department resolveDepartment(Employee entity) {
        if (entity.getDepartment() != null && entity.getDepartment().getId() != null) {
            return departmentRepository.findById(entity.getDepartment().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(Department.class, entity.getDepartment().getId()));
        }
        if (entity.getDepartmentId() != null) {
            return departmentRepository.findById(entity.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException(Department.class, entity.getDepartmentId()));
        }
        return null;
    }

    private Position resolvePosition(Employee entity) {
        if (entity.getPosition() != null && entity.getPosition().getId() != null) {
            return positionRepository.findById(entity.getPosition().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(Position.class, entity.getPosition().getId()));
        }
        if (entity.getPositionId() != null) {
            return positionRepository.findById(entity.getPositionId())
                    .orElseThrow(() -> new ResourceNotFoundException(Position.class, entity.getPositionId()));
        }
        return null;
    }
}
