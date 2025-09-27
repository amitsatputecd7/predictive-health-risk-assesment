package com.hackforjob.metlife.predictive_health_risk_assesment.controller;

import com.hackforjob.metlife.predictive_health_risk_assesment.dto.LoginRequest;
import com.hackforjob.metlife.predictive_health_risk_assesment.dto.LoginResponse;
import com.hackforjob.metlife.predictive_health_risk_assesment.entity.User;
import com.hackforjob.metlife.predictive_health_risk_assesment.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // Allow CORS for development
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            log.info("Login request received for userId: {}", loginRequest.getUserId());
            LoginResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Login failed for userId: {}, error: {}", loginRequest.getUserId(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Authentication failed", e.getMessage()));
        }
    }
    
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Invalid token", "Authorization header must start with Bearer"));
            }
            
            String token = authHeader.substring(7);
            boolean isValid = authService.validateToken(token);
            
            if (isValid) {
                String userId = authService.getUserIdFromToken(token);
                return ResponseEntity.ok(new TokenValidationResponse(true, "Token is valid", userId));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Invalid token", "Token is expired or invalid"));
            }
        } catch (Exception e) {
            log.error("Token validation error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Token validation failed", e.getMessage()));
        }
    }
    
    // Demo endpoint to create test users
    @PostMapping("/create-demo-user")
    public ResponseEntity<?> createDemoUser(
            @RequestParam String userId,
            @RequestParam String password,
            @RequestParam String fullName) {
        try {
            User user = authService.createDemoUser(userId, password, fullName);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new DemoUserResponse(user.getUserId(), user.getFullName(), "Demo user created successfully"));
        } catch (RuntimeException e) {
            log.error("Demo user creation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("User creation failed", e.getMessage()));
        }
    }
    
    // Health check for auth service
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Authentication service is running!");
    }
    
    // Inner classes for responses
    public static class ErrorResponse {
        private String error;
        private String message;
        
        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }
        
        public String getError() { return error; }
        public String getMessage() { return message; }
    }
    
    public static class TokenValidationResponse {
        private boolean valid;
        private String message;
        private String userId;
        
        public TokenValidationResponse(boolean valid, String message, String userId) {
            this.valid = valid;
            this.message = message;
            this.userId = userId;
        }
        
        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
        public String getUserId() { return userId; }
    }
    
    public static class DemoUserResponse {
        private String userId;
        private String fullName;
        private String message;
        
        public DemoUserResponse(String userId, String fullName, String message) {
            this.userId = userId;
            this.fullName = fullName;
            this.message = message;
        }
        
        public String getUserId() { return userId; }
        public String getFullName() { return fullName; }
        public String getMessage() { return message; }
    }
}