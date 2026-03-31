package com.hrm.hrm_api.common.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerBadRequestTest {

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new BadRequestProbeController())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

    @Test
    void badRequestException_returnsBadRequestInsteadOfUnauthorized() throws Exception {
        mockMvc.perform(get("/test/bad-request").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Yêu cầu không hợp lệ: payload invalid"));
    }

    @RestController
    static class BadRequestProbeController {
        @GetMapping("/test/bad-request")
        String trigger() {
            throw new BadRequestException("payload invalid");
        }
    }
}
