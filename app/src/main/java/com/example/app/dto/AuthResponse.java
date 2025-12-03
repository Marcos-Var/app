package com.example.app.dto;

public class AuthResponse {
    private String token;
    private String email;
    private String name;
    
    // Constructores
    public AuthResponse() {
    }
    
    public AuthResponse(String token, String email, String name) {
        this.token = token;
        this.email = email;
        this.name = name;
    }
    
    // Getters y Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
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
}
