package com.hrm.hrm_api.modules.service.position;

import com.hrm.hrm_api.modules.entity.Position;
import com.hrm.hrm_api.modules.repository.PositionRepository;
import com.hrm.hrm_api.modules.service.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class PositionServiceImpl extends AbstractCrudService<Position> implements PositionService {

    public PositionServiceImpl(PositionRepository repository) {
        super(repository, Position.class);
    }
}
