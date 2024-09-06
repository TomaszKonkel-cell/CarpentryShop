package com.example.carpentry.Request;

import java.util.HashSet;
import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SignUpRequest {

    @NotBlank(message = "Nazwa użytkownika jest wymagana.")
    @Size(min = 3, max = 20, message = "Nazwa musi mieć od 3 do 20 znaków")
    private String username;

    @NotBlank(message = "Hasło jest wymagane.")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!*()]).{8,}$", message = "Hasło musi mieć 8 znaków, zawierać dużą i małą litere oraz znak specjalny.")
    private String password;

    private Set<String> roles = new HashSet<>();

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    

}
