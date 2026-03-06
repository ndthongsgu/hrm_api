package com.hrm.hrm_api.modules.service.payrollperiod;

import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.payrollperiod.PayrollPeriodFilter;
import com.hrm.hrm_api.modules.dto.request.PayrollPeriodSearchRequest;
import com.hrm.hrm_api.modules.entity.PayrollPeriod;
import com.hrm.hrm_api.modules.service.CrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PayrollPeriodService extends CrudService<PayrollPeriod> {
    Page<PayrollPeriod> findAll(PayrollPeriodFilter filter, Pageable pageable);
    PaginationResponse<PayrollPeriod> search(PayrollPeriodSearchRequest request, Pageable pageable);
    void summarizeAttendance(UUID payrollPeriodId);
    void calculatePayslips(UUID payrollPeriodId);
    void lockPeriod(UUID payrollPeriodId);
    void deactivate(UUID payrollPeriodId);
    void activate(UUID payrollPeriodId);
}