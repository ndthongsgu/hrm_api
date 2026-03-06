package com.hrm.hrm_api.modules.service.position;

import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.request.PositionSearchRequest;
import com.hrm.hrm_api.modules.entity.Position;
import com.hrm.hrm_api.modules.repository.PositionRepository;
import com.hrm.hrm_api.modules.service.AbstractCrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PositionServiceImpl extends AbstractCrudService<Position> implements PositionService {

    private final PositionRepository positionRepository;

    public PositionServiceImpl(PositionRepository repository) {
        super(repository, Position.class);
        this.positionRepository = repository;
    }

    @Override
    public PaginationResponse<Position> search(PositionSearchRequest request) {
        if (request == null) {
            request = new PositionSearchRequest();
            request.setSkip(0);
            request.setTake(20);
        }

        int skip = request.getSkip() != null ? request.getSkip() : 0;
        int take = request.getTake() != null && request.getTake() > 0 ? request.getTake() : 20;

        if (skip < 0) skip = 0;
        if (take <= 0) take = 20;
        if (take > 100) take = 100;

        int pageNumber = skip / take;
        int pageSize = take;

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "created_at"));

        var filter = request.getWhere();

        Page<Position> positions = positionRepository.findByFilters(
            filter != null ? filter.getName() : null,
            filter != null ? filter.getIsDeleted() : null,
            pageable
        );

        return PaginationResponse.of(
            positions.getContent(),
            positions.getTotalElements(),
            pageNumber,
            pageSize
        );
    }
}
