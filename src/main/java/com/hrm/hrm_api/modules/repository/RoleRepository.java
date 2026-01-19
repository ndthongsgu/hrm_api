package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
