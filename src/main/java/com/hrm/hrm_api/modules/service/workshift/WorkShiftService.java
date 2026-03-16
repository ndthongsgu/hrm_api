package com.hrm.hrm_api.modules.service.workshift;

import com.hrm.hrm_api.modules.entity.WorkShift;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface WorkShiftService {
    Page<WorkShift> findAll(Pageable pageable);
    WorkShift findById(UUID id);
    WorkShift create(WorkShift entity);
    WorkShift update(UUID id, WorkShift entity);
    void delete(UUID id);
}