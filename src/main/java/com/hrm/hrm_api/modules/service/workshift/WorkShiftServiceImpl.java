package com.hrm.hrm_api.modules.service.workshift;

import com.hrm.hrm_api.modules.entity.WorkShift;
import com.hrm.hrm_api.modules.repository.WorkShiftRepository;
import com.hrm.hrm_api.modules.service.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class WorkShiftServiceImpl extends AbstractCrudService<WorkShift> implements WorkShiftService {

    public WorkShiftServiceImpl(WorkShiftRepository repository) {
        super(repository, WorkShift.class);
    }
}