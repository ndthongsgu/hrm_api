package com.hrm.hrm_api.config;

import com.hrm.hrm_api.common.security.UserDetailsImpl;
import com.hrm.hrm_api.config.annotations.RequirePermission;
import com.hrm.hrm_api.common.constants.PermissionDefinition;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PermissionAspect {

    @Before("@annotation(requirePermission)")
    public void checkPermission(JoinPoint joinPoint, RequirePermission requirePermission) {
        String module = requirePermission.module();
        PermissionDefinition action = requirePermission.action();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String requiredAuthority = module + "_" + action.getCode();

            boolean hasPermission = userDetails.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(requiredAuthority));

            if (!hasPermission) {
                throw new RuntimeException("Access denied: Insufficient permissions for " + requiredAuthority);
            }
        } else {
            throw new RuntimeException("User not authenticated");
        }
    }
}
