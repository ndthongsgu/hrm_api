package com.hrm.hrm_api.modules.dto.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseRequest {
    private String requestId;
    private String locale;
    private Instant timestamp = Instant.now();
}