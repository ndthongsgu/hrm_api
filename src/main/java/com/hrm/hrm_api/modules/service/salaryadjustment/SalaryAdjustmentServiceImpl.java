package com.hrm.hrm_api.modules.service.salaryadjustment;

import com.hrm.hrm_api.modules.entity.SalaryAdjustment;
import com.hrm.hrm_api.modules.repository.SalaryAdjustmentRepository;
import com.hrm.hrm_api.modules.service.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class SalaryAdjustmentServiceImpl extends AbstractCrudService<SalaryAdjustment> implements SalaryAdjustmentService {

    public SalaryAdjustmentServiceImpl(SalaryAdjustmentRepository repository) {
        super(repository, SalaryAdjustment.class);
    }
}