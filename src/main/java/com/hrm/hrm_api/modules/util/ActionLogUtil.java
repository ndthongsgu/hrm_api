package com.hrm.hrm_api.modules.util;

import com.hrm.hrm_api.common.security.SecurityUtil;
import com.hrm.hrm_api.modules.entity.ActionLog;
import com.hrm.hrm_api.modules.service.actionlog.ActionLogService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ActionLogUtil {

    private final ActionLogService actionLogService;
    private final SecurityUtil securityUtil;

    public ActionLogUtil(ActionLogService actionLogService, SecurityUtil securityUtil) {
        this.actionLogService = actionLogService;
        this.securityUtil = securityUtil;
    }

    /**
     * Log an action for a specific entity
     */
    public void logAction(String functionId, String functionType, String actionType, 
                         String description, String createdByName, String employeeCode) {
        ActionLog actionLog = new ActionLog();
        actionLog.setFunctionId(functionId);
        actionLog.setFunctionType(functionType);
        actionLog.setType(actionType);
        actionLog.setDescription(description);
        actionLog.setCreatedByName(createdByName != null ? createdByName : securityUtil.getCurrentUsername());
        actionLog.setEmployeeCode(employeeCode);
        
        actionLogService.create(actionLog);
    }

    /**
     * Log a create action
     */
    public void logCreate(UUID entityId, String functionType, String description, String createdByName) {
        logAction(entityId.toString(), functionType, "Create", description, createdByName, null);
    }

    /**
     * Log an update action
     */
    public void logUpdate(UUID entityId, String functionType, String description, String updatedByName) {
        logAction(entityId.toString(), functionType, "Update", description, updatedByName, null);
    }

    /**
     * Log a delete action
     */
    public void logDelete(UUID entityId, String functionType, String description, String deletedByName) {
        logAction(entityId.toString(), functionType, "Delete", description, deletedByName, null);
    }

    /**
     * Log an approve action
     */
    public void logApprove(UUID entityId, String functionType, String description, String approvedByName) {
        logAction(entityId.toString(), functionType, "Approve", description, approvedByName, null);
    }

    /**
     * Log a cancel action
     */
    public void logCancel(UUID entityId, String functionType, String description, String cancelledByName) {
        logAction(entityId.toString(), functionType, "Cancel", description, cancelledByName, null);
    }

    /**
     * Log an activate action
     */
    public void logActivate(UUID entityId, String functionType, String description, String activatedByName) {
        logAction(entityId.toString(), functionType, "Activate", description, activatedByName, null);
    }

    /**
     * Log a deactivate action
     */
    public void logDeactivate(UUID entityId, String functionType, String description, String deactivatedByName) {
        logAction(entityId.toString(), functionType, "Deactivate", description, deactivatedByName, null);
    }
}
