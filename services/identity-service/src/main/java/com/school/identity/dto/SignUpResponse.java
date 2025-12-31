package com.school.identity.dto;

import com.school.identity.domain.UserStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public class SignUpResponse {

    private UUID id;
    private String username;
    private String email;
    private String first_name;
    private String last_name;
    private String phone;
    private UserStatus status;
    private LocalDateTime created_at;

    // Constructors
    public SignUpResponse() {
    }

    public SignUpResponse(UUID id, String username, String email, String first_name,
                         String last_name, String phone, UserStatus status, LocalDateTime created_at) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.phone = phone;
        this.status = status;
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

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }
}

