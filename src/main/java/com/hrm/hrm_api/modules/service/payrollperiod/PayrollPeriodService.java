package com.hrm.hrm_api.modules.service.payrollperiod;

import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.base.SelectOptionDto;
import com.hrm.hrm_api.modules.dto.payrollperiod.PayrollPeriodFilter;
import com.hrm.hrm_api.modules.dto.request.PayrollPeriodSearchRequest;
import com.hrm.hrm_api.modules.entity.PayrollPeriod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PayrollPeriodService {
    Page<PayrollPeriod> findAll(Pageable pageable);
    PayrollPeriod findById(UUID id);
    PayrollPeriod create(PayrollPeriod entity);
    PayrollPeriod update(UUID id, PayrollPeriod entity);
    void delete(UUID id);
    Page<PayrollPeriod> findAll(PayrollPeriodFilter filter, Pageable pageable);
    PaginationResponse<PayrollPeriod> search(PayrollPeriodSearchRequest request, Pageable pageable);
    List<SelectOptionDto> getSelectOptions();
    void summarizeAttendance(UUID payrollPeriodId);
    void activate(UUID payrollPeriodId);
    void deactivate(UUID payrollPeriodId);
    void approve(UUID id);
}