package com.hrm.hrm_api.modules.service.user;

import com.hrm.hrm_api.modules.entity.User;
import com.hrm.hrm_api.modules.repository.UserRepository;
import com.hrm.hrm_api.modules.service.AbstractCrudService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl extends AbstractCrudService<User> implements UserService {

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder) {
        super(repository, User.class);
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User create(User entity) {
        if (entity.getPassword() != null && !entity.getPassword().isEmpty()) {
            entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        }
        return super.create(entity);
    }

    @Override
    @Transactional
    public User update(Long id, User entity) {
        if (entity.getPassword() != null && !entity.getPassword().isEmpty()) {
            entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        } else {
            // don't overwrite password if not provided
            User existing = findById(id);
            entity.setPassword(existing.getPassword());
        }
        return super.update(id, entity);
    }
}