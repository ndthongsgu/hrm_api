package com.hrm.hrm_api.modules.service.dailyattendance;

import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import com.hrm.hrm_api.modules.entity.DailyAttendance;
import com.hrm.hrm_api.modules.repository.DailyAttendanceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DailyAttendanceServiceImpl implements DailyAttendanceService {

    private final DailyAttendanceRepository repository;

    public DailyAttendanceServiceImpl(DailyAttendanceRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<DailyAttendance> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public DailyAttendance findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(DailyAttendance.class, id));
    }

    @Override
    @Transactional
    public DailyAttendance create(DailyAttendance entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public DailyAttendance update(UUID id, DailyAttendance entity) {
        findById(id);
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(DailyAttendance.class, id);
        }
        repository.deleteById(id);
    }
}