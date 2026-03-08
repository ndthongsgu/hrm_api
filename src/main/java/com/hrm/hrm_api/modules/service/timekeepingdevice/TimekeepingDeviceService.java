package com.hrm.hrm_api.modules.service.timekeepingdevice;

import com.hrm.hrm_api.modules.entity.TimekeepingDevice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TimekeepingDeviceService {
    Page<TimekeepingDevice> findAll(Pageable pageable);
    TimekeepingDevice findById(UUID id);
    TimekeepingDevice create(TimekeepingDevice entity);
    TimekeepingDevice update(UUID id, TimekeepingDevice entity);
    void delete(UUID id);
}