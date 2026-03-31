package com.hrm.hrm_api.common.exception;

import com.hrm.hrm_api.modules.dto.base.BaseResponse;
import com.hrm.hrm_api.modules.dto.base.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.TransactionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponse("Không tìm thấy", ex.getMessage(), Instant.now()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return new ResponseEntity<>(new ErrorResponse("Lỗi kiểm định", msg, Instant.now()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        return new ResponseEntity<>(new ErrorResponse("Lỗi kiểm định", ex.getMessage(), Instant.now()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<BaseResponse<Object>> handleBadRequest(BadRequestException ex) {
        return new ResponseEntity<>(
                BaseResponse.error("Yêu cầu không hợp lệ: " + ex.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<BaseResponse<Object>> handleBadCredentials(BadCredentialsException ex) {
        return new ResponseEntity<>(BaseResponse.error("Tên đăng nhập hoặc mật khẩu không chính xác"), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<BaseResponse<Object>> handleAuthentication(AuthenticationException ex) {
        return new ResponseEntity<>(BaseResponse.error("Xác thực thất bại: " + ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<BaseResponse<Object>> handleInsufficientAuthentication(InsufficientAuthenticationException ex) {
        return new ResponseEntity<>(BaseResponse.error("Người dùng chưa xác thực"), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseResponse<Object>> handleAccessDenied(AccessDeniedException ex) {
        return new ResponseEntity<>(BaseResponse.error("Truy cập bị từ chối: " + ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    /**
     * Xử lý lỗi SQL/Database queries
     */
    @ExceptionHandler(InvalidDataAccessResourceUsageException.class)
    public ResponseEntity<BaseResponse<Object>> handleInvalidDataAccess(InvalidDataAccessResourceUsageException ex) {
        String message = ex.getMessage();
        // Trích xuất lỗi SQL từ message
        if (message != null && message.contains("[")) {
            String sqlError = message.substring(message.indexOf("["), Math.min(message.lastIndexOf("]") + 1, message.length()));
            return new ResponseEntity<>(
                    BaseResponse.error("Lỗi truy vấn cơ sở dữ liệu: " + sqlError),
                    HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(
                BaseResponse.error("Lỗi truy vấn cơ sở dữ liệu: " + message),
                HttpStatus.BAD_REQUEST);
    }

    /**
     * Xử lý lỗi ràng buộc dữ liệu (unique, foreign key, etc.)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<BaseResponse<Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = bestMessage(ex);
        String lower = message.toLowerCase();
        if (lower.contains("unique")) {
            return new ResponseEntity<>(
                    BaseResponse.error("Bản ghi này đã tồn tại. Vui lòng sử dụng giá trị khác."),
                    HttpStatus.CONFLICT);
        }
        if (lower.contains("foreign key")) {
            return new ResponseEntity<>(
                    BaseResponse.error("Không thể xóa hoặc sửa bản ghi này do có dữ liệu liên quan."),
                    HttpStatus.CONFLICT);
        }
        if (lower.contains("not-null") || lower.contains("null value")) {
            return new ResponseEntity<>(
                    BaseResponse.error("Thiếu dữ liệu bắt buộc: " + message),
                    HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(
                BaseResponse.error("Vi phạm tính toàn vẹn dữ liệu: " + message),
                HttpStatus.CONFLICT);
    }

    /**
     * Xử lý các lỗi Database khác
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<BaseResponse<Object>> handleDataAccess(DataAccessException ex) {
        return new ResponseEntity<>(
                BaseResponse.error("Lỗi cơ sở dữ liệu: " + bestMessage(ex)),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Xử lý lỗi Transaction
     */
    @ExceptionHandler(TransactionException.class)
    public ResponseEntity<BaseResponse<Object>> handleTransaction(TransactionException ex) {
        String detail = bestMessage(ex);
        return new ResponseEntity<>(
            BaseResponse.error("Lỗi giao dịch: " + detail),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Xử lý các lỗi IllegalStateException, IllegalArgumentException
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<BaseResponse<Object>> handleIllegalState(IllegalStateException ex) {
        return new ResponseEntity<>(
                BaseResponse.error("Thao tác không hợp lệ: " + ex.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse<Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return new ResponseEntity<>(
                BaseResponse.error("Tham số không hợp lệ: " + ex.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BaseResponse<Object>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return new ResponseEntity<>(
                BaseResponse.error("Tham số không hợp lệ: " + ex.getValue()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PdfGenerationException.class)
    public ResponseEntity<BaseResponse<Object>> handlePdfGeneration(PdfGenerationException ex) {
        return new ResponseEntity<>(
                BaseResponse.error(ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Xử lý lỗi NullPointerException
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<BaseResponse<Object>> handleNullPointer(NullPointerException ex) {
        return new ResponseEntity<>(
                BaseResponse.error("Dữ liệu không hợp lệ: Trường bắt buộc bị thiếu hoặc null"),
                HttpStatus.BAD_REQUEST);
    }

    /**
     * Xử lý tất cả các lỗi chung
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Object>> handleGeneric(Exception ex) {
        ex.printStackTrace();
        return new ResponseEntity<>(
                BaseResponse.error("Lỗi máy chủ nội bộ: " + bestMessage(ex)),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String bestMessage(Throwable ex) {
        Throwable root = getRootCause(ex);
        String rootMessage = root != null ? root.getMessage() : null;
        if (rootMessage != null && !rootMessage.isBlank()) {
            return rootMessage;
        }
        String message = ex.getMessage();
        return message != null && !message.isBlank() ? message : "Đã xảy ra lỗi không mong muốn";
    }

    private Throwable getRootCause(Throwable throwable) {
        Throwable result = throwable;
        while (result != null && result.getCause() != null && result.getCause() != result) {
            result = result.getCause();
        }
        return result;
    }
}
