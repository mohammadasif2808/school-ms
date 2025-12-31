package com.school.identity.dto;

import com.school.identity.domain.UserStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class CurrentUserResponse {

    private UUID id;
    private String username;
    private String email;
    private String first_name;
    private String last_name;
    private String phone;
    private String avatar_url;
    private Boolean is_super_admin;
    private UserStatus status;
    private String role;
    private List<String> permissions;
    private LocalDateTime created_at;

    // Constructors
    public CurrentUserResponse() {
    }

    public CurrentUserResponse(UUID id, String username, String email, String first_name,
                              String last_name, String phone, String avatar_url,
                              Boolean is_super_admin, UserStatus status, String role,
                              List<String> permissions, LocalDateTime created_at) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.phone = phone;
        this.avatar_url = avatar_url;
        this.is_super_admin = is_super_admin;
        this.status = status;
        this.role = role;
        this.permissions = permissions;
        this.created_at = created_at;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public Boolean getIs_super_admin() {
        return is_super_admin;
    }

    public void setIs_super_admin(Boolean is_super_admin) {
        this.is_super_admin = is_super_admin;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
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

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }
}

