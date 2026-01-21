package com.hrm.hrm_api.common.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hrm.hrm_api.modules.entity.Employee;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String email;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String username, String email, String password,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(Employee employee) {
        throw new UnsupportedOperationException("Authentication via Employee entity is not supported. Use User entity instead.");
    }

    private static java.util.List<String> parsePermissionsJson(String json) {
        if (json == null || json.isEmpty()) return java.util.Collections.emptyList();
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            java.util.Map<String, java.util.List<String>> map = mapper.readValue(json, java.util.Map.class);
            java.util.List<String> result = new java.util.ArrayList<>();
            for (java.util.Map.Entry<String, java.util.List<String>> e : map.entrySet()) {
                String module = e.getKey();
                for (Object actionObj : e.getValue()) {
                    String action = String.valueOf(actionObj);
                    result.add(module + "_" + action);
                }
            }
            return result;
        } catch (Exception ex) {
            return java.util.Collections.emptyList();
        }
    }

    public static UserDetailsImpl buildFromUser(com.hrm.hrm_api.modules.entity.User user) {
        java.util.List<GrantedAuthority> authorities = user.getRoles() == null ? java.util.Collections.emptyList()
                : user.getRoles().stream()
                .flatMap(role -> parsePermissionsJson(role.getPermissionsJson()).stream())
                .map(perm -> new SimpleGrantedAuthority(perm))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getUsername(),
                user.getPassword(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}
