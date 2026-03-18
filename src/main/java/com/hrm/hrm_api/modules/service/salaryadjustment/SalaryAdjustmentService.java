package com.hrm.hrm_api.modules.service.salaryadjustment;

import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.request.SalaryAdjustmentCreateRequest;
import com.hrm.hrm_api.modules.dto.request.SalaryAdjustmentSearchRequest;
import com.hrm.hrm_api.modules.entity.SalaryAdjustment;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SalaryAdjustmentService {
    SalaryAdjustment findById(UUID id);
    SalaryAdjustment create(SalaryAdjustmentCreateRequest request);
    SalaryAdjustment update(UUID id, SalaryAdjustmentCreateRequest request);
    PaginationResponse<SalaryAdjustment> search(SalaryAdjustmentSearchRequest request, Pageable pageable);
    void deactivate(UUID id);
    void activate(UUID id);
    void approve(UUID id);
    void unApprove(UUID id);
}