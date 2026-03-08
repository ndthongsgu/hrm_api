package com.hrm.hrm_api.modules.dto.base;

import java.util.UUID;

public class SelectOptionDto {
    private UUID id;
    private String name;

    public SelectOptionDto() {}

    public SelectOptionDto(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}