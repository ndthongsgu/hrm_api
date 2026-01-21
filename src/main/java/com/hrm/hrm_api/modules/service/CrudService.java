package com.hrm.hrm_api.modules.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CrudService<T> {
    Page<T> findAll(Pageable pageable);
    T findById(Long id);
    T create(T entity);
    T update(Long id, T entity);
    void delete(Long id);
}
