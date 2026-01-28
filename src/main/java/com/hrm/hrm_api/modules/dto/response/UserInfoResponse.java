package com.hrm.hrm_api.modules.dto.response;

import java.util.Map;
import java.util.List;

public class UserInfoResponse {
    private String userId;
    private String username;
    private String email;
    private String name;
    private boolean isAdmin;
    private boolean isGoogle;
    // Permissions in JSON format: {"EMPLOYEE": ["READ","CREATE"], "PAYROLL": ["READ"]}
    private Map<String, List<String>> permissions;

    public UserInfoResponse() {
    }

    public UserInfoResponse(String userId, String username, String email, String name, boolean isAdmin, boolean isGoogle, Map<String, List<String>> permissions) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.name = name;
        this.isAdmin = isAdmin;
        this.isGoogle = isGoogle;
        this.permissions = permissions;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isGoogle() {
        return isGoogle;
    }

    public void setGoogle(boolean google) {
        isGoogle = google;
    }

    public Map<String, List<String>> getPermissions() {
        return permissions;
    }

    public void setPermissions(Map<String, List<String>> permissions) {
        this.permissions = permissions;
    }
}
