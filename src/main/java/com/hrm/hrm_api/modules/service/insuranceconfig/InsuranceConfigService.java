package com.hrm.hrm_api.modules.service.insuranceconfig;

import com.hrm.hrm_api.modules.entity.InsuranceConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface InsuranceConfigService {
    Page<InsuranceConfig> findAll(Pageable pageable);
    InsuranceConfig findById(UUID id);
    InsuranceConfig create(InsuranceConfig entity);
    InsuranceConfig update(UUID id, InsuranceConfig entity);
    void delete(UUID id);
}