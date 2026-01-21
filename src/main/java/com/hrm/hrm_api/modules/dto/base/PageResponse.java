package com.hrm.hrm_api.modules.dto.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResponse<T> {
    private List<T> items;
    private long totalElements;
    private int totalPages;
    private int page;
    private int size;

    public static <T> PageResponse<T> of(List<T> items, long totalElements, int page, int size) {
        int totalPages = (int) Math.ceil((double) totalElements / (double) size);
        return new PageResponse<>(items, totalElements, totalPages, page, size);
    }
}