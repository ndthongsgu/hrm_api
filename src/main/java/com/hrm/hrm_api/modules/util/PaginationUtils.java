package com.hrm.hrm_api.modules.util;

import com.hrm.hrm_api.modules.dto.base.PageResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public class PaginationUtils {
    public static <T> PageResponse<T> toPageResponse(Page<T> page) {
        List<T> items = page.getContent();
        return PageResponse.of(items, page.getTotalElements(), page.getNumber(), page.getSize());
    }
}