package com.hrm.hrm_api.modules.service.user;

import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import com.hrm.hrm_api.modules.entity.Role;
import com.hrm.hrm_api.modules.entity.User;
import com.hrm.hrm_api.modules.entity.UserRole;
import com.hrm.hrm_api.modules.repository.RoleRepository;
import com.hrm.hrm_api.modules.repository.UserRepository;
import com.hrm.hrm_api.modules.repository.UserRoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository repository, RoleRepository roleRepository, UserRoleRepository userRoleRepository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public User findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(User.class, id));
    }

    @Override
    @Transactional
    public User create(User entity) {
        if (entity.getPassword() != null && !entity.getPassword().isEmpty()) {
            entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        }
        return repository.save(entity);
    }

    @Override
    @Transactional
    public User update(UUID id, User entity) {
        if (entity.getPassword() != null && !entity.getPassword().isEmpty()) {
            entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        } else {
            User existing = findById(id);
            entity.setPassword(existing.getPassword());
        }
        findById(id);
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    @Transactional
    public User assignRoles(UUID id, List<UUID> roleIds) {
        // Remove all current assignments using a direct join-table delete.
        // This avoids versioned orphan-removal deletes and duplicate insert ordering issues.
        userRoleRepository.deleteByUserId(id);

        if (roleIds != null && !roleIds.isEmpty()) {
            User user = repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(User.class, id));

            List<UserRole> newAssignments = new ArrayList<>();
            for (UUID roleId : roleIds) {
                Role role = roleRepository.findById(roleId)
                        .orElseThrow(() -> new ResourceNotFoundException(Role.class, roleId));
                newAssignments.add(new UserRole(user, role));
            }

            userRoleRepository.saveAll(newAssignments);
        }

        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, id));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(User.class, id);
        }
        repository.deleteById(id);
    }
}