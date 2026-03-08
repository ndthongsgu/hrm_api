package com.hrm.hrm_api.modules.service.position;

import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.request.PositionSearchRequest;
import com.hrm.hrm_api.modules.entity.Position;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PositionService {
    Page<Position> findAll(Pageable pageable);
    Position findById(UUID id);
    Position create(Position entity);
    Position update(UUID id, Position entity);
    void delete(UUID id);
    PaginationResponse<Position> search(PositionSearchRequest request);
}
