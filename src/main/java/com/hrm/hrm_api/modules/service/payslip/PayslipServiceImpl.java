package com.hrm.hrm_api.modules.service.payslip;

import com.hrm.hrm_api.modules.entity.Payslip;
import com.hrm.hrm_api.modules.repository.PayslipRepository;
import com.hrm.hrm_api.modules.service.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class PayslipServiceImpl extends AbstractCrudService<Payslip> implements PayslipService {

    public PayslipServiceImpl(PayslipRepository repository) {
        super(repository, Payslip.class);
    }
}