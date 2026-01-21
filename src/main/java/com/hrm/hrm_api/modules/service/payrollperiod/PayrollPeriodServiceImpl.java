package com.hrm.hrm_api.modules.service.payrollperiod;

import com.hrm.hrm_api.modules.entity.PayrollPeriod;
import com.hrm.hrm_api.modules.repository.PayrollPeriodRepository;
import com.hrm.hrm_api.modules.service.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class PayrollPeriodServiceImpl extends AbstractCrudService<PayrollPeriod> implements PayrollPeriodService {

    public PayrollPeriodServiceImpl(PayrollPeriodRepository repository) {
        super(repository, PayrollPeriod.class);
    }
}