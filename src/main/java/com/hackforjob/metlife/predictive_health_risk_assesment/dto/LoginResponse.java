package com.hackforjob.metlife.predictive_health_risk_assesment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    private String token;
    private String tokenType = "Bearer";
    private Long userId;
    private String email;
    private String username;
    private String message;
    
    public LoginResponse(String token, Long userId, String email, String username, String message) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.message = message;
    }
}