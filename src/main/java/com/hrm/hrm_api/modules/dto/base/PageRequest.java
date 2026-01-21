package com.hrm.hrm_api.modules.dto.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest {
    private int page = 0;        // zero-based page index
    private int size = 20;       // page size
    private String sort;         // e.g. "name,asc"
    private String query;        // free text search
}