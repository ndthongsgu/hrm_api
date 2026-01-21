package com.hrm.hrm_api.modules.service.dailyattendance;

import com.hrm.hrm_api.modules.entity.DailyAttendance;
import com.hrm.hrm_api.modules.repository.DailyAttendanceRepository;
import com.hrm.hrm_api.modules.service.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class DailyAttendanceServiceImpl extends AbstractCrudService<DailyAttendance> implements DailyAttendanceService {

    public DailyAttendanceServiceImpl(DailyAttendanceRepository repository) {
        super(repository, DailyAttendance.class);
    }
}