package com.hrm.hrm_api.modules.service.allowancetype;

import com.hrm.hrm_api.modules.entity.AllowanceType;
import com.hrm.hrm_api.modules.repository.AllowanceTypeRepository;
import com.hrm.hrm_api.modules.service.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class AllowanceTypeServiceImpl extends AbstractCrudService<AllowanceType> implements AllowanceTypeService {

    public AllowanceTypeServiceImpl(AllowanceTypeRepository repository) {
        super(repository, AllowanceType.class);
    }
}