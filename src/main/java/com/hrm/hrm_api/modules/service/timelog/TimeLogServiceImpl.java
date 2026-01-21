package com.hrm.hrm_api.modules.service.timelog;

import com.hrm.hrm_api.modules.entity.TimeLog;
import com.hrm.hrm_api.modules.repository.TimeLogRepository;
import com.hrm.hrm_api.modules.service.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class TimeLogServiceImpl extends AbstractCrudService<TimeLog> implements TimeLogService {

    public TimeLogServiceImpl(TimeLogRepository repository) {
        super(repository, TimeLog.class);
    }
}