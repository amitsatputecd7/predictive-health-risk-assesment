package com.hackforjob.metlife.predictive_health_risk_assesment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    private String token;
    private String tokenType = "Bearer";
    private String userId;
    private String message;
    private LocalDateTime expiresAt;
    
    public LoginResponse(String token, String userId, String message, LocalDateTime expiresAt) {
        this.token = token;
        this.userId = userId;
        this.message = message;
        this.expiresAt = expiresAt;
    }
}
