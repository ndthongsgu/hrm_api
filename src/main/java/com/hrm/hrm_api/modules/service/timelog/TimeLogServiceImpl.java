package com.hrm.hrm_api.modules.service.timelog;

import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import com.hrm.hrm_api.modules.entity.TimeLog;
import com.hrm.hrm_api.modules.repository.TimeLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class TimeLogServiceImpl implements TimeLogService {

    private final TimeLogRepository repository;

    public TimeLogServiceImpl(TimeLogRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<TimeLog> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public TimeLog findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(TimeLog.class, id));
    }

    @Override
    @Transactional
    public TimeLog create(TimeLog entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public TimeLog update(UUID id, TimeLog entity) {
        findById(id);
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(TimeLog.class, id);
        }
        repository.deleteById(id);
    }
}