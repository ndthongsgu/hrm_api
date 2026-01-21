package com.hrm.hrm_api.modules.service.timekeepingdevice;

import com.hrm.hrm_api.modules.entity.TimekeepingDevice;
import com.hrm.hrm_api.modules.repository.TimekeepingDeviceRepository;
import com.hrm.hrm_api.modules.service.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class TimekeepingDeviceServiceImpl extends AbstractCrudService<TimekeepingDevice> implements TimekeepingDeviceService {

    public TimekeepingDeviceServiceImpl(TimekeepingDeviceRepository repository) {
        super(repository, TimekeepingDevice.class);
    }
}