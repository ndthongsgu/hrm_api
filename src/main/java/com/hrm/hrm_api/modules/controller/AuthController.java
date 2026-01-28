package com.hrm.hrm_api.modules.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrm.hrm_api.common.response.JwtResponse;
import com.hrm.hrm_api.common.security.UserDetailsImpl;
import com.hrm.hrm_api.common.security.UserDetailsServiceImpl;
import com.hrm.hrm_api.common.util.JwtUtils;
import com.hrm.hrm_api.modules.dto.base.BaseResponse;
import com.hrm.hrm_api.modules.dto.request.LoginRequest;
import com.hrm.hrm_api.modules.dto.request.RefreshTokenRequest;
import com.hrm.hrm_api.modules.dto.response.UserInfoResponse;
import com.hrm.hrm_api.modules.entity.Role;
import com.hrm.hrm_api.modules.entity.User;
import com.hrm.hrm_api.modules.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService, UserRepository userRepository, ObjectMapper objectMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        // Get role names (e.g., "ADMIN", "USER") from User entity, not permissions
        Optional<User> userOpt = userRepository.findById(userDetails.getId());
        java.util.List<String> roleNames = new ArrayList<>();
        Boolean isAdmin = false;
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            isAdmin = Boolean.TRUE.equals(user.getIsAdmin());
            if (user.getRoles() != null) {
                roleNames = user.getRoles().stream()
                        .map(Role::getName)
                        .filter(name -> name != null && !name.isEmpty())
                        .collect(java.util.stream.Collectors.toList());
            }
        }

        return ResponseEntity.ok(new JwtResponse(jwt, refreshToken, userDetails.getId().toString(), userDetails.getUsername(), userDetails.getEmail(), roleNames, isAdmin));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            String refreshToken = refreshTokenRequest.getRefreshToken();

            if (refreshToken == null || refreshToken.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Refresh token is required");
            }

            // Validate refresh token
            if (!jwtUtils.validateJwtToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid refresh token");
            }

            // Extract username from refresh token
            String username = jwtUtils.getUserNameFromJwtToken(refreshToken);

            // Load user details
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;

            // Create authentication object with UserDetailsImpl as principal
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetailsImpl, null, userDetailsImpl.getAuthorities()
            );

            // Generate new access token
            String newAccessToken = jwtUtils.generateJwtToken(authentication);

            // Generate new refresh token (rotate refresh token for security)
            String newRefreshToken = jwtUtils.generateRefreshToken(authentication);

            // Get role names (e.g., "ADMIN", "USER") from User entity, not permissions
            Optional<User> userOpt = userRepository.findById(userDetailsImpl.getId());
            java.util.List<String> roleNames = new ArrayList<>();
            Boolean isAdmin = false;
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                isAdmin = Boolean.TRUE.equals(user.getIsAdmin());
                if (user.getRoles() != null) {
                    roleNames = user.getRoles().stream()
                            .map(Role::getName)
                            .filter(name -> name != null && !name.isEmpty())
                            .collect(java.util.stream.Collectors.toList());
                }
            }

            return ResponseEntity.ok(new JwtResponse(
                    newAccessToken,
                    newRefreshToken,
                    userDetailsImpl.getId().toString(),
                    username,
                    userDetailsImpl.getEmail(),
                    roleNames,
                    isAdmin
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired refresh token: " + e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<BaseResponse<UserInfoResponse>> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(BaseResponse.error("User not authenticated"));
            }

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Optional<User> userOpt = userRepository.findById(userDetails.getId());
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(BaseResponse.error("User not found"));
            }

            User user = userOpt.get();
            Map<String, List<String>> permissions = parsePermissionsFromRoles(user.getRoles());

            // Get email from employee if available, otherwise use username
            String email = "";
            if (user.getEmployee() != null && user.getEmployee().getEmail() != null) {
                email = user.getEmployee().getEmail();
            } else {
                email = user.getUsername(); // Fallback to username
            }

            UserInfoResponse userInfo = new UserInfoResponse(
                    user.getId().toString(),
                    user.getUsername(),
                    email,
                    user.getUsername(),
                    Boolean.TRUE.equals(user.getIsAdmin()),
                    false, // isGoogle - not supported yet
                    permissions
            );

            return ResponseEntity.ok(BaseResponse.ok(userInfo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponse.error("Failed to get user info: " + e.getMessage()));
        }
    }

    /**
     * Merge permissions from user's roles into a single JSON object
     * Role.permissionsJson format: {"EMPLOYEE": ["READ","CREATE"], "PAYROLL": ["READ"]}
     * Returns merged Map<ModuleCode, List<Actions>> with all unique actions from all roles
     */
    private Map<String, List<String>> parsePermissionsFromRoles(Set<Role> roles) {
        Map<String, Set<String>> permissionsMap = new HashMap<>();

        if (roles == null || roles.isEmpty()) {
            return new HashMap<>();
        }

        // Merge permissions from all roles
        for (Role role : roles) {
            if (role.getPermissionsJson() == null || role.getPermissionsJson().isEmpty()) {
                continue;
            }

            try {
                // Parse JSON: {"EMPLOYEE": ["READ","CREATE"], "PAYROLL": ["READ"]}
                Map<String, List<String>> modulePermissions = objectMapper.readValue(
                        role.getPermissionsJson(),
                        objectMapper.getTypeFactory().constructMapType(Map.class, String.class, List.class)
                );

                // Merge permissions: combine actions from all roles for each module
                for (Map.Entry<String, List<String>> entry : modulePermissions.entrySet()) {
                    String moduleCode = entry.getKey();
                    List<String> actions = entry.getValue();

                    // Use Set to avoid duplicate actions
                    Set<String> moduleActions = permissionsMap.getOrDefault(moduleCode, new HashSet<>());
                    for (String action : actions) {
                        moduleActions.add(action.toUpperCase()); // Normalize to uppercase
                    }
                    permissionsMap.put(moduleCode, moduleActions);
                }
            } catch (Exception e) {
                // Log error but continue processing other roles
                System.err.println("Failed to parse permissions from role " + role.getName() + ": " + e.getMessage());
            }
        }

        // Convert Set to List for JSON serialization
        Map<String, List<String>> result = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : permissionsMap.entrySet()) {
            result.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }

        return result;
    }
}