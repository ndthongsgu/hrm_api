package com.hrm.hrm_api.modules.service.attendancesummary;

import com.hrm.hrm_api.modules.entity.AttendanceSummary;
import com.hrm.hrm_api.modules.repository.AttendanceSummaryRepository;
import com.hrm.hrm_api.modules.service.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class AttendanceSummaryServiceImpl extends AbstractCrudService<AttendanceSummary> implements AttendanceSummaryService {

    public AttendanceSummaryServiceImpl(AttendanceSummaryRepository repository) {
        super(repository, AttendanceSummary.class);
    }
}