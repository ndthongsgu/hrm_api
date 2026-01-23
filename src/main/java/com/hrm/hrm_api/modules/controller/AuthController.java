package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.common.response.JwtResponse;
import com.hrm.hrm_api.common.security.UserDetailsImpl;
import com.hrm.hrm_api.common.security.UserDetailsServiceImpl;
import com.hrm.hrm_api.common.util.JwtUtils;
import com.hrm.hrm_api.modules.dto.request.LoginRequest;
import com.hrm.hrm_api.modules.dto.request.RefreshTokenRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(authentication);

        com.hrm.hrm_api.common.security.UserDetailsImpl userDetails = (com.hrm.hrm_api.common.security.UserDetailsImpl) authentication.getPrincipal();
        java.util.List<String> roles = userDetails.getAuthorities().stream().map(a -> a.getAuthority()).toList();

        return ResponseEntity.ok(new JwtResponse(jwt, refreshToken, userDetails.getId().toString(), userDetails.getUsername(), userDetails.getEmail(), roles));
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

            // Get roles
            java.util.List<String> roles = userDetailsImpl.getAuthorities().stream()
                    .map(a -> a.getAuthority()).toList();

            return ResponseEntity.ok(new JwtResponse(
                    newAccessToken,
                    newRefreshToken,
                    userDetailsImpl.getId().toString(),
                    username,
                    userDetailsImpl.getEmail(),
                    roles
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired refresh token: " + e.getMessage());
        }
    }
}