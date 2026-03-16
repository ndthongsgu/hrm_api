package com.hrm.hrm_api.modules.service.timekeepingdevice;

import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import com.hrm.hrm_api.modules.entity.TimekeepingDevice;
import com.hrm.hrm_api.modules.repository.TimekeepingDeviceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class TimekeepingDeviceServiceImpl implements TimekeepingDeviceService {

    private final TimekeepingDeviceRepository repository;

    public TimekeepingDeviceServiceImpl(TimekeepingDeviceRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<TimekeepingDevice> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public TimekeepingDevice findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(TimekeepingDevice.class, id));
    }

    @Override
    @Transactional
    public TimekeepingDevice create(TimekeepingDevice entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public TimekeepingDevice update(UUID id, TimekeepingDevice entity) {
        findById(id);
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(TimekeepingDevice.class, id);
        }
        repository.deleteById(id);
    }
}