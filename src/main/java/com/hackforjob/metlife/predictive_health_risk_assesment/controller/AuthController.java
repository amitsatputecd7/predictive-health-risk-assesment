package com.hackforjob.metlife.predictive_health_risk_assesment.controller;

import com.hackforjob.metlife.predictive_health_risk_assesment.dto.LoginRequest;
import com.hackforjob.metlife.predictive_health_risk_assesment.dto.LoginResponse;
import com.hackforjob.metlife.predictive_health_risk_assesment.dto.UserRegistrationRequest;
import com.hackforjob.metlife.predictive_health_risk_assesment.entity.User;
import com.hackforjob.metlife.predictive_health_risk_assesment.exception.ErrorResponse;
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
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            log.info("Login request received for email: {}", loginRequest.getEmail());
            LoginResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Login failed for email: {}", loginRequest.getEmail(), e);
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .error("Login Failed")
                    .message(e.getMessage())
                    .path("/api/v1/auth/login")
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegistrationRequest registrationRequest) {
        try {
            log.info("Registration request received for email: {}", registrationRequest.getEmail());
            User user = authService.registerUser(registrationRequest);
            
            LoginResponse response = new LoginResponse();
            response.setUserId(user.getId());
            response.setEmail(user.getEmail());
            response.setUsername(user.getUsername());
            response.setMessage("User registered successfully");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Registration failed for email: {}", registrationRequest.getEmail(), e);
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .error("Registration Failed")
                    .message(e.getMessage())
                    .path("/api/v1/auth/register")
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            return authService.findUserById(id)
                    .map(user -> {
                        LoginResponse response = new LoginResponse();
                        response.setUserId(user.getId());
                        response.setEmail(user.getEmail());
                        response.setUsername(user.getUsername());
                        response.setMessage("User found");
                        return ResponseEntity.ok(response);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error finding user by id: {}", id, e);
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .error("Internal Server Error")
                    .message("Error retrieving user")
                    .path("/api/v1/auth/user/" + id)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ErrorResponse.builder()
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .error("Invalid Token")
                                .message("Authorization header must start with Bearer")
                                .path("/api/v1/auth/validate-token")
                                .build());
            }
            
            String token = authHeader.substring(7);
            String email = authService.getEmailFromToken(token);
            Long userId = authService.getUserIdFromToken(token);
            
            if (authService.validateToken(token, email)) {
                LoginResponse response = new LoginResponse();
                response.setUserId(userId);
                response.setEmail(email);
                response.setMessage("Token is valid");
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ErrorResponse.builder()
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .error("Invalid Token")
                                .message("Token is expired or invalid")
                                .path("/api/v1/auth/validate-token")
                                .build());
            }
        } catch (Exception e) {
            log.error("Error validating token", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ErrorResponse.builder()
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .error("Invalid Token")
                            .message("Token validation failed")
                            .path("/api/v1/auth/validate-token")
                            .build());
        }
    }
}
