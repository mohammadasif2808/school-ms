package com.school.identity.dto;

import java.util.List;
import java.util.UUID;

public class JwtClaims {

    private UUID userId;
    private String username;
    private String role;
    private List<String> permissions;
    private String tenantId;
    private long iat;
    private long exp;

    // Constructors
    public JwtClaims() {
    }

    public JwtClaims(UUID userId, String username, String role, List<String> permissions,
                     String tenantId, long iat, long exp) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.permissions = permissions;
        this.tenantId = tenantId;
        this.iat = iat;
        this.exp = exp;
    }

    // Getters and Setters
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public long getIat() {
        return iat;
    }

    public void setIat(long iat) {
        this.iat = iat;
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }
}

