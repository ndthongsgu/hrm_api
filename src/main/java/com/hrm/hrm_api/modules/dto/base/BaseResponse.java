package com.hrm.hrm_api.modules.dto.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {
    private boolean success = true;
    private String message;
    private T data;
    private Instant timestamp = Instant.now();

    public static <T> BaseResponse<T> ok(T data) {
        BaseResponse<T> res = new BaseResponse<>();
        res.setSuccess(true);
        res.setData(data);
        return res;
    }

    public static <T> BaseResponse<T> error(String message) {
        BaseResponse<T> res = new BaseResponse<>();
        res.setSuccess(false);
        res.setMessage(message);
        return res;
    }
}