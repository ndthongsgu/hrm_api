package com.hrm.hrm_api.modules.service.user;

import com.hrm.hrm_api.modules.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    Page<User> findAll(Pageable pageable);
    User findById(UUID id);
    User create(User entity);
    User update(UUID id, User entity);
    void delete(UUID id);
}