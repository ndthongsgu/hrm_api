package com.hrm.hrm_api.modules.service.payrollperiod;

import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.base.SelectOptionDto;
import com.hrm.hrm_api.modules.dto.request.PayrollPeriodSearchRequest;
import com.hrm.hrm_api.modules.entity.PayrollPeriod;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PayrollPeriodService {
    PaginationResponse<PayrollPeriod> search(PayrollPeriodSearchRequest request, Pageable pageable);
    PayrollPeriod findById(UUID id);
    PayrollPeriod create(PayrollPeriod entity);
    PayrollPeriod update(UUID id, PayrollPeriod entity);
    List<SelectOptionDto> getSelectOptions();
    void activate(UUID payrollPeriodId);
    void deactivate(UUID payrollPeriodId);
    void approve(UUID id);
    void unApprove(UUID id);
    List<SelectOptionDto> getMobileSelectOptions();
    void bulkApprove(List<UUID> ids);
    void bulkUnApprove(List<UUID> ids);
}