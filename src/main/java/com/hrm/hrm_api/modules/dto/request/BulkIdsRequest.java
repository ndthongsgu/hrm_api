package com.hrm.hrm_api.modules.dto.request;

import java.util.List;
import java.util.UUID;

public class BulkIdsRequest {
    private List<UUID> ids;

    public BulkIdsRequest() {}

    public BulkIdsRequest(List<UUID> ids) {
        this.ids = ids;
    }

    public List<UUID> getIds() {
        return ids;
    }

    public void setIds(List<UUID> ids) {
        this.ids = ids;
    }
}