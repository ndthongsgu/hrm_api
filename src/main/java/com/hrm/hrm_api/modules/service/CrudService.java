package com.hrm.hrm_api.modules.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CrudService<T> {
    Page<T> findAll(Pageable pageable);
    T findById(UUID id);
    T create(T entity);
    T update(UUID id, T entity);
    void delete(UUID id);
}
