package com.hrm.hrm_api.common.exception;

import com.hrm.hrm_api.modules.dto.base.BaseResponse;
import com.hrm.hrm_api.modules.dto.base.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponse("NOT_FOUND", ex.getMessage(), Instant.now()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return new ResponseEntity<>(new ErrorResponse("VALIDATION_ERROR", msg, Instant.now()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        return new ResponseEntity<>(new ErrorResponse("VALIDATION_ERROR", ex.getMessage(), Instant.now()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<BaseResponse<Object>> handleBadCredentials(BadCredentialsException ex) {
        return new ResponseEntity<>(BaseResponse.error("Invalid username or password"), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<BaseResponse<Object>> handleAuthentication(AuthenticationException ex) {
        return new ResponseEntity<>(BaseResponse.error("Authentication failed: " + ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<BaseResponse<Object>> handleInsufficientAuthentication(InsufficientAuthenticationException ex) {
        return new ResponseEntity<>(BaseResponse.error("User not authenticated"), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseResponse<Object>> handleAccessDenied(AccessDeniedException ex) {
        return new ResponseEntity<>(BaseResponse.error("Access denied: " + ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Object>> handleGeneric(Exception ex) {
        ex.printStackTrace();
        return new ResponseEntity<>(BaseResponse.error("Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
