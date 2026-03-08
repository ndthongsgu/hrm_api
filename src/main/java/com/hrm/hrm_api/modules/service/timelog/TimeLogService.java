package com.hrm.hrm_api.modules.service.timelog;

import com.hrm.hrm_api.modules.entity.TimeLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TimeLogService {
    Page<TimeLog> findAll(Pageable pageable);
    TimeLog findById(UUID id);
    TimeLog create(TimeLog entity);
    TimeLog update(UUID id, TimeLog entity);
    void delete(UUID id);
}