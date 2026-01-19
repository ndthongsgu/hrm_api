package com.hrm.hrm_api.config.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation để tự động inject thông tin user hiện tại vào parameter của controller method
 *
 * Example:
 * public ResponseEntity<?> someMethod(@CurrentUser UserDbModel user) {
 *     // user sẽ tự động được inject
 * }
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUser {
}

