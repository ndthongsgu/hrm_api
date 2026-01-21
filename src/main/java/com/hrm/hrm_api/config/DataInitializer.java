package com.hrm.hrm_api.config;

import com.hrm.hrm_api.common.constants.ModuleDefinition;
import com.hrm.hrm_api.common.constants.PermissionDefinition;
import com.hrm.hrm_api.modules.entity.Role;
import com.hrm.hrm_api.modules.entity.User;
import com.hrm.hrm_api.modules.repository.RoleRepository;
import com.hrm.hrm_api.modules.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
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

        // Create default admin user
        if (userRepository.findByUsername("admin").isEmpty()) {
            User u = new User();
            u.setUsername("admin");
            u.setPassword("admin"); // TODO: encode password in production
            u.setActive(true);
            u.setRoles(Set.of(adminRole));
            userRepository.save(u);
        }
    }
} 