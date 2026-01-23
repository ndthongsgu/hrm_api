package com.hrm.hrm_api.modules.dto.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}