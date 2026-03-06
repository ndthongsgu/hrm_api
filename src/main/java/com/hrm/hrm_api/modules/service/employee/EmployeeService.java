package com.hrm.hrm_api.modules.service.employee;

import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.request.EmployeeSearchRequest;
import com.hrm.hrm_api.modules.entity.Employee;
import com.hrm.hrm_api.modules.service.CrudService;

public interface EmployeeService extends CrudService<Employee> {
    PaginationResponse<Employee> search(EmployeeSearchRequest request);
}
