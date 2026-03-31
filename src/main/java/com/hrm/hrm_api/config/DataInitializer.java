package com.hrm.hrm_api.config;

import com.hrm.hrm_api.common.constants.ModuleDefinition;
import com.hrm.hrm_api.common.constants.PermissionDefinition;
import com.hrm.hrm_api.modules.entity.Employee;
import com.hrm.hrm_api.modules.entity.Role;
import com.hrm.hrm_api.modules.entity.User;
import com.hrm.hrm_api.modules.repository.EmployeeRepository;
import com.hrm.hrm_api.modules.repository.RoleRepository;
import com.hrm.hrm_api.modules.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository,
                          EmployeeRepository employeeRepository,
                          PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        // Build permissions map from enums
        java.util.Map<String, java.util.List<String>> perms = new java.util.LinkedHashMap<>();
        for (ModuleDefinition module : ModuleDefinition.values()) {
            java.util.List<String> actions = new java.util.ArrayList<>();
            for (PermissionDefinition action : PermissionDefinition.values()) {
                actions.add(action.getCode());
            }
            perms.put(module.getCode(), actions);
        }

        String allPermissionsJson = mapper.writeValueAsString(perms);

        // Create ADMIN role with all permissions JSON
        Optional<Role> adminRoleOpt = roleRepository.findAll().stream().filter(r -> "ADMIN".equals(r.getName())).findFirst();
        Role adminRole = adminRoleOpt.orElseGet(() -> {
            Role r = new Role();
            r.setName("ADMIN");
            r.setDescription("System administrator with full permissions");
            r.setPermissionsJson(allPermissionsJson);
            return roleRepository.save(r);
        });

        if (adminRole.getPermissionsJson() == null || adminRole.getPermissionsJson().isEmpty()) {
            adminRole.setPermissionsJson(allPermissionsJson);
            roleRepository.save(adminRole);
        }

        // Create default admin user and assign ADMIN role
        User adminUser;
        if (userRepository.findByUsername("admin").isEmpty()) {
            User u = new User();
            u.setUsername("admin");
            u.setPassword(passwordEncoder.encode("admin"));
            u.setActive(true);
            u.setIsAdmin(true);
            u.addRole(adminRole);
            adminUser = userRepository.save(u);
        } else {
            adminUser = userRepository.findByUsername("admin").get();
            boolean hasAdminRole = adminUser.getRoles().stream()
                    .anyMatch(r -> "ADMIN".equals(r.getName()));
            if (!hasAdminRole) {
                adminUser.addRole(adminRole);
                adminUser = userRepository.save(adminUser);
            }
        }

        if (adminUser.getEmployee() == null) {
            Employee employee = employeeRepository.findByEmail("admin@local").orElseGet(() -> {
                Employee e = new Employee();
                e.setFullName("System Admin");
                e.setEmployeeCode("ADMIN_PROFILE");
                e.setEmail("admin@local");
                e.setPhone("0000000000");
                e.setStatus("ACTIVE");
                return employeeRepository.save(e);
            });

            adminUser.setEmployee(employee);
            userRepository.save(adminUser);
        }
    }
} 