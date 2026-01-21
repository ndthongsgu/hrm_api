package com.hrm.hrm_api.common.security;

import com.hrm.hrm_api.modules.entity.Employee;
import com.hrm.hrm_api.modules.entity.User;
import com.hrm.hrm_api.modules.repository.EmployeeRepository;
import com.hrm.hrm_api.modules.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try to find by username in users table first
        java.util.Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            return UserDetailsImpl.buildFromUser(userOpt.get());
        }

        // Currently Employee entity does not contain authentication fields (password/roles).
        // Treat as not found for authentication.
        throw new UsernameNotFoundException("User Not Found with username: " + username);
    }

    // Add method to EmployeeRepository
}
