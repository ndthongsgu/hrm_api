package com.hrm.hrm_api.modules.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrm.hrm_api.common.security.UserDetailsImpl;
import com.hrm.hrm_api.common.security.UserDetailsServiceImpl;
import com.hrm.hrm_api.common.util.JwtUtils;
import com.hrm.hrm_api.modules.entity.Employee;
import com.hrm.hrm_api.modules.entity.User;
import com.hrm.hrm_api.modules.repository.EmployeeRepository;
import com.hrm.hrm_api.modules.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerChangePasswordTest {

    private final AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    private final JwtUtils jwtUtils = mock(JwtUtils.class);
    private final UserDetailsServiceImpl userDetailsService = mock(UserDetailsServiceImpl.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

    private final AuthController controller = new AuthController(
            authenticationManager,
            jwtUtils,
            userDetailsService,
            userRepository,
                employeeRepository,
            new ObjectMapper(),
            passwordEncoder
    );

    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void changePassword_returnsUnauthorized_whenUserNotAuthenticated() throws Exception {
        mockMvc.perform(post("/api/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"oldPassword\":\"old123\",\"newPassword\":\"new123\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("User not authenticated"));
    }

    @Test
    void updateMe_returnsUnauthorized_whenUserNotAuthenticated() throws Exception {
        mockMvc.perform(put("/api/auth/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"Nguyen Van A\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("User not authenticated"));
    }

    @Test
    void updateMe_updatesEmployeeProfile_whenInputValid() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();
        authenticateAs(userId, "PAYROLL_READ");

        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setFullName("Old Name");
        employee.setEmail("old@example.com");

        User user = new User();
        user.setId(userId);
        user.setUsername("admin");
        user.setEmployee(employee);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(employeeRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/auth/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"New Name\",\"email\":\"new@example.com\",\"phone\":\"0123456789\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andExpect(jsonPath("$.data.name").value("New Name"))
                .andExpect(jsonPath("$.data.email").value("new@example.com"));

        verify(employeeRepository).save(employee);
        verify(userRepository).save(user);
    }

    @Test
    void updateMe_returnsBadRequest_whenUserHasNoEmployeeButProfileFieldsProvided() throws Exception {
        UUID userId = UUID.randomUUID();
        authenticateAs(userId, "PAYROLL_READ");

        User user = new User();
        user.setId(userId);
        user.setUsername("admin");
        user.setEmployee(null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        mockMvc.perform(put("/api/auth/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"New Name\",\"email\":\"new@example.com\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Tài khoản chưa liên kết nhân viên, không thể cập nhật thông tin cá nhân"));
    }

    @Test
    void changePassword_returnsBadRequest_whenOldPasswordIncorrect() throws Exception {
        UUID userId = UUID.randomUUID();
        authenticateAs(userId, "PAYROLL_READ");

        User user = new User();
        user.setId(userId);
        user.setUsername("admin");
        user.setPassword("encoded-old");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old123", "encoded-old")).thenReturn(false);

        mockMvc.perform(post("/api/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"oldPassword\":\"old123\",\"newPassword\":\"new123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Mật khẩu cũ không chính xác"));
    }

    @Test
    void changePassword_returnsOk_andUpdatesPassword_whenInputValid() throws Exception {
        UUID userId = UUID.randomUUID();
        authenticateAs(userId, "PAYROLL_READ");

        User user = new User();
        user.setId(userId);
        user.setUsername("admin");
        user.setPassword("encoded-old");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old123", "encoded-old")).thenReturn(true);
        when(passwordEncoder.encode("new123")).thenReturn("encoded-new");

        mockMvc.perform(post("/api/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"oldPassword\":\"old123\",\"newPassword\":\"new123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("Đổi mật khẩu thành công"));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("encoded-new", userCaptor.getValue().getPassword());
        assertEquals(userId, userCaptor.getValue().getId());
    }

    private void authenticateAs(UUID userId, String authority) {
        UserDetailsImpl principal = new UserDetailsImpl(
                userId,
                "admin",
                "admin",
                "secret",
                List.of(new SimpleGrantedAuthority(authority)),
                false
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal,
                "secret",
                principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }
}
