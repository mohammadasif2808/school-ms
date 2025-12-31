package com.school.identity.dto;

import com.school.identity.domain.UserStatus;
import java.util.UUID;

public class SignInResponse {

    private String accessToken;
    private UserInfo user;

    // Constructors
    public SignInResponse() {
    }

    public SignInResponse(String accessToken, UserInfo user) {
        this.accessToken = accessToken;
        this.user = user;
    }

    // Getters and Setters
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    // Inner class for user info
    public static class UserInfo {
        private UUID id;
        private String username;
        private String email;
        private String first_name;
        private String last_name;
        private String avatar_url;
        private UserStatus status;

        // Constructors
        public UserInfo() {
        }

        public UserInfo(UUID id, String username, String email, String first_name,
                       String last_name, String avatar_url, UserStatus status) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.first_name = first_name;
            this.last_name = last_name;
            this.avatar_url = avatar_url;
            this.status = status;
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

        public String getAvatar_url() {
            return avatar_url;
        }

        public void setAvatar_url(String avatar_url) {
            this.avatar_url = avatar_url;
        }

        public UserStatus getStatus() {
            return status;
        }

        public void setStatus(UserStatus status) {
            this.status = status;
        }
    }
}

