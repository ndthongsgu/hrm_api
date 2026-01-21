package com.hrm.hrm_api.modules.service.syssetting;

import com.hrm.hrm_api.modules.entity.SysSetting;
import com.hrm.hrm_api.modules.repository.SysSettingRepository;
import com.hrm.hrm_api.modules.service.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class SysSettingServiceImpl extends AbstractCrudService<SysSetting> implements SysSettingService {

    public SysSettingServiceImpl(SysSettingRepository repository) {
        super(repository, SysSetting.class);
    }
}