package com.hrm.hrm_api.modules.service.employeeshift;

import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.base.SelectOptionDto;
import com.hrm.hrm_api.modules.dto.request.EmployeeShiftAssignmentCreateRequest;
import com.hrm.hrm_api.modules.dto.request.EmployeeShiftAssignmentSearchRequest;
import com.hrm.hrm_api.modules.dto.response.EmployeeShiftAssignmentDto;
import com.hrm.hrm_api.modules.dto.workshift.EmployeeShiftAssignmentFilter;
import com.hrm.hrm_api.modules.entity.Employee;
import com.hrm.hrm_api.modules.entity.EmployeeShiftAssignment;
import com.hrm.hrm_api.modules.entity.WorkShift;
import com.hrm.hrm_api.modules.repository.EmployeeRepository;
import com.hrm.hrm_api.modules.repository.EmployeeShiftAssignmentRepository;
import com.hrm.hrm_api.modules.repository.WorkShiftRepository;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmployeeShiftAssignmentServiceImpl implements EmployeeShiftAssignmentService {

    private final EmployeeShiftAssignmentRepository repository;
    private final EmployeeRepository employeeRepository;
    private final WorkShiftRepository workShiftRepository;
    private final EntityManager entityManager;

    public EmployeeShiftAssignmentServiceImpl(
            EmployeeShiftAssignmentRepository repository,
            EmployeeRepository employeeRepository,
            WorkShiftRepository workShiftRepository,
            EntityManager entityManager) {
        this.repository = repository;
        this.employeeRepository = employeeRepository;
        this.workShiftRepository = workShiftRepository;
        this.entityManager = entityManager;
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
    public EmployeeShiftAssignment create(EmployeeShiftAssignmentCreateRequest request) {
        // Find and validate employee
        Employee employee = entityManager.find(Employee.class, request.getEmployeeId());
        if (employee == null) {
            throw new ResourceNotFoundException(Employee.class, request.getEmployeeId());
        }

        // Find and validate shift
        WorkShift shift = entityManager.find(WorkShift.class, request.getShiftId());
        if (shift == null) {
            throw new ResourceNotFoundException(WorkShift.class, request.getShiftId());
        }

        // Create new assignment
        EmployeeShiftAssignment assignment = new EmployeeShiftAssignment();
        assignment.setEmployee(employee);
        assignment.setShift(shift);
        assignment.setStartDate(request.getStartDate());
        assignment.setEndDate(request.getEndDate());

        return repository.save(assignment);
    }

    @Override
    @Transactional
    public EmployeeShiftAssignment update(UUID id, EmployeeShiftAssignmentCreateRequest request) {
        // Find existing assignment
        EmployeeShiftAssignment assignment = findById(id);

        // Find and validate employee
        Employee employee = entityManager.find(Employee.class, request.getEmployeeId());
        if (employee == null) {
            throw new ResourceNotFoundException(Employee.class, request.getEmployeeId());
        }

        // Find and validate shift
        WorkShift shift = entityManager.find(WorkShift.class, request.getShiftId());
        if (shift == null) {
            throw new ResourceNotFoundException(WorkShift.class, request.getShiftId());
        }

        // Update assignment
        assignment.setEmployee(employee);
        assignment.setShift(shift);
        assignment.setStartDate(request.getStartDate());
        assignment.setEndDate(request.getEndDate());

        return repository.save(assignment);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(EmployeeShiftAssignment.class, id);
        }
        repository.deleteById(id);
    }

    /**
     * Build specification for employee shift assignment search
     */
    private Specification<EmployeeShiftAssignment> buildSpec(EmployeeShiftAssignmentFilter filter) {
        return (root, query, cb) -> {
            if (filter == null) {
                return cb.conjunction();
            }

            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();

            if (filter.getEmployeeCode() != null && !filter.getEmployeeCode().isEmpty()) {
                predicates.add(cb.like(
                        cb.lower(root.get("employee").get("code")),
                        "%" + filter.getEmployeeCode().toLowerCase() + "%"
                ));
            }

            if (filter.getEmployeeName() != null && !filter.getEmployeeName().isEmpty()) {
                predicates.add(cb.like(
                        cb.lower(root.get("employee").get("fullName")),
                        "%" + filter.getEmployeeName().toLowerCase() + "%"
                ));
            }

            if (filter.getShiftId() != null) {
                predicates.add(cb.equal(root.get("shift").get("id"), filter.getShiftId()));
            }

            if (filter.getShiftName() != null && !filter.getShiftName().isEmpty()) {
                predicates.add(cb.like(
                        cb.lower(root.get("shift").get("name")),
                        "%" + filter.getShiftName().toLowerCase() + "%"
                ));
            }

            if (filter.getEmployeeId() != null) {
                predicates.add(cb.equal(root.get("employee").get("id"), filter.getEmployeeId()));
            }

            if (filter.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startDate"), filter.getStartDate()));
            }

            if (filter.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("endDate"), filter.getEndDate()));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

    @Override
    public PaginationResponse<EmployeeShiftAssignmentDto> search(EmployeeShiftAssignmentSearchRequest request, Pageable pageable) {
        EmployeeShiftAssignmentFilter filter = request != null ? request.getWhere() : null;
        Page<EmployeeShiftAssignment> assignments = repository.findAll(buildSpec(filter), pageable);

        List<EmployeeShiftAssignmentDto> dtoList = assignments.getContent().stream()
                .map(assignment -> new EmployeeShiftAssignmentDto(
                        assignment.getId(),
                        assignment.getEmployee().getId(),
                        assignment.getEmployee().getEmployeeCode(),
                        assignment.getEmployee().getFullName(),
                        assignment.getShift().getId(),
                        assignment.getShift().getName(),
                        assignment.getStartDate(),
                        assignment.getEndDate()
                ))
                .collect(Collectors.toList());

        return PaginationResponse.of(
                dtoList,
                assignments.getTotalElements(),
                assignments.getNumber(),
                assignments.getSize());
    }

    @Override
    public List<SelectOptionDto> getSelectOptions() {
        List<EmployeeShiftAssignment> assignments = repository.findAll();

        return assignments.stream()
                .map(assignment -> new SelectOptionDto(
                        assignment.getId(),
                        assignment.getEmployee().getFullName() + " - " + assignment.getShift().getName()
                ))
                .collect(Collectors.toList());
    }
}