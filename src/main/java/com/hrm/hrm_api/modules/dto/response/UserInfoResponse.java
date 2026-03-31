package com.hrm.hrm_api.modules.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hrm.hrm_api.modules.entity.Employee;
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
    private EmployeeInfo employee;

    public static class EmployeeInfo {
        private String id;
        private String employeeCode;
        private String fullName;
        private String email;
        private String phone;
        private String address;
        private String status;

        public EmployeeInfo() {
        }

        public EmployeeInfo(String id, String employeeCode, String fullName, String email, String phone, String address, String status) {
            this.id = id;
            this.employeeCode = employeeCode;
            this.fullName = fullName;
            this.email = email;
            this.phone = phone;
            this.address = address;
            this.status = status;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getEmployeeCode() {
            return employeeCode;
        }

        public void setEmployeeCode(String employeeCode) {
            this.employeeCode = employeeCode;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public UserInfoResponse() {
    }

    public UserInfoResponse(String userId, String username, String email, String name, boolean isAdmin, boolean isGoogle, Map<String, List<String>> permissions) {
        this(userId, username, email, name, isAdmin, isGoogle, permissions, null);
    }

    public UserInfoResponse(String userId, String username, String email, String name, boolean isAdmin, boolean isGoogle, Map<String, List<String>> permissions, EmployeeInfo employee) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.name = name;
        this.isAdmin = isAdmin;
        this.isGoogle = isGoogle;
        this.permissions = permissions;
        this.employee = employee;
    }

    public static EmployeeInfo fromEmployee(Employee employee) {
        if (employee == null) {
            return null;
        }
        return new EmployeeInfo(
                employee.getId() != null ? employee.getId().toString() : null,
                employee.getEmployeeCode(),
                employee.getFullName(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getAddress(),
                employee.getStatus());
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

    @JsonProperty("isAdmin")
    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    @JsonProperty("isGoogle")
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

    public EmployeeInfo getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeeInfo employee) {
        this.employee = employee;
    }
}
