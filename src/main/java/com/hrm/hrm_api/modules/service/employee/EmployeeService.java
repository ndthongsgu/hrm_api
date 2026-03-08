package com.hrm.hrm_api.modules.service.employee;

import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.base.SelectOptionDto;
import com.hrm.hrm_api.modules.dto.request.EmployeeSearchRequest;
import com.hrm.hrm_api.modules.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {
    Page<Employee> findAll(Pageable pageable);
    Employee findById(UUID id);
    Employee create(Employee entity);
    Employee update(UUID id, Employee entity);
    void delete(UUID id);
    PaginationResponse<Employee> search(EmployeeSearchRequest request);
    List<SelectOptionDto> getSelectOptions();
}
