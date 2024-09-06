package com.example.carpentry.Request;

import java.util.List;

public class LoggedUser {
    private Long id;
    private String username;
    private List<String> roles;
    private Long loginTime;
    private String token;

    public LoggedUser(Long id, String username, List<String> roles, Long loginTime, String token) {
        this.id = id;
        this.username = username;
        this.roles = roles;
        this.loginTime = loginTime;
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Long loginTime) {
        this.loginTime = loginTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
