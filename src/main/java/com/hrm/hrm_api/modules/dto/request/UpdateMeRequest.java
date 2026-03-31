package com.hrm.hrm_api.modules.dto.request;

import lombok.Data;

@Data
public class UpdateMeRequest {
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String address;
}
