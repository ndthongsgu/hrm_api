package com.hrm.hrm_api.modules.service.insuranceconfig;

import com.hrm.hrm_api.modules.entity.InsuranceConfig;
import com.hrm.hrm_api.modules.repository.InsuranceConfigRepository;
import com.hrm.hrm_api.modules.service.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class InsuranceConfigServiceImpl extends AbstractCrudService<InsuranceConfig> implements InsuranceConfigService {

    public InsuranceConfigServiceImpl(InsuranceConfigRepository repository) {
        super(repository, InsuranceConfig.class);
    }
}