package com.hrm.hrm_api.modules.service.syssetting;

import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import com.hrm.hrm_api.modules.entity.SysSetting;
import com.hrm.hrm_api.modules.repository.SysSettingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SysSettingServiceImpl implements SysSettingService {

    private final SysSettingRepository repository;

    public SysSettingServiceImpl(SysSettingRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<SysSetting> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public SysSetting findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(SysSetting.class, id));
    }

    @Override
    @Transactional
    public SysSetting create(SysSetting entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public SysSetting update(UUID id, SysSetting entity) {
        findById(id);
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(SysSetting.class, id);
        }
        repository.deleteById(id);
    }
}