package com.hrm.hrm_api.modules.service.position;

import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.request.PositionSearchRequest;
import com.hrm.hrm_api.modules.entity.Position;
import com.hrm.hrm_api.modules.service.CrudService;

public interface PositionService extends CrudService<Position> {
    PaginationResponse<Position> search(PositionSearchRequest request);
}
