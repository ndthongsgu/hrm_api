package com.hrm.hrm_api.modules.service.employee;

import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.request.EmployeeSearchRequest;
import com.hrm.hrm_api.modules.entity.Employee;
import com.hrm.hrm_api.modules.repository.EmployeeRepository;
import com.hrm.hrm_api.modules.service.AbstractCrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends AbstractCrudService<Employee> implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository repository) {
        super(repository, Employee.class);
        this.employeeRepository = repository;
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
}
