package com.hrm.hrm_api.modules.service.dependent;

import com.hrm.hrm_api.modules.entity.Dependent;
import com.hrm.hrm_api.modules.repository.DependentRepository;
import com.hrm.hrm_api.modules.service.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class DependentServiceImpl extends AbstractCrudService<Dependent> implements DependentService {

    public DependentServiceImpl(DependentRepository repository) {
        super(repository, Dependent.class);
    }
}
