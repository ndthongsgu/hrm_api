package com.hrm.hrm_api.modules.service.salaryadjustment;

import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.request.SalaryAdjustmentCreateRequest;
import com.hrm.hrm_api.modules.dto.request.SalaryAdjustmentSearchRequest;
import com.hrm.hrm_api.modules.dto.salaryadjustment.SalaryAdjustmentFilter;
import com.hrm.hrm_api.modules.entity.SalaryAdjustment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface SalaryAdjustmentService {
    Page<SalaryAdjustment> findAll(Pageable pageable);
    SalaryAdjustment findById(UUID id);
    void delete(UUID id);
    SalaryAdjustment create(SalaryAdjustmentCreateRequest request);
    SalaryAdjustment update(UUID id, SalaryAdjustmentCreateRequest request);
    SalaryAdjustment upsertByPayrollPeriodAndEmployee(UUID payrollPeriodId, UUID employeeId, SalaryAdjustment payload);
    List<SalaryAdjustment> batchUpsertByPayrollPeriod(UUID payrollPeriodId, List<SalaryAdjustment> adjustments);
    Page<SalaryAdjustment> findAll(SalaryAdjustmentFilter filter, Pageable pageable);
    PaginationResponse<SalaryAdjustment> search(SalaryAdjustmentSearchRequest request, Pageable pageable);
    void deactivate(UUID id);
    void activate(UUID id);
}