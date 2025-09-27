package com.hackforjob.metlife.predictive_health_risk_assesment.service;

import com.hackforjob.metlife.predictive_health_risk_assesment.dto.LoginRequest;
import com.hackforjob.metlife.predictive_health_risk_assesment.dto.LoginResponse;
import com.hackforjob.metlife.predictive_health_risk_assesment.entity.User;
import com.hackforjob.metlife.predictive_health_risk_assesment.repository.UserRepository;
import com.hackforjob.metlife.predictive_health_risk_assesment.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("Login attempt for userId: {}", loginRequest.getUserId());
        
        // Find user by userId
        Optional<User> userOptional = userRepository.findByUserIdAndActive(loginRequest.getUserId(), true);
        
        if (userOptional.isEmpty()) {
            log.warn("User not found or inactive: {}", loginRequest.getUserId());
            throw new RuntimeException("Invalid credentials");
        }
        
        User user = userOptional.get();
        
        // Verify password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            log.warn("Invalid password for userId: {}", loginRequest.getUserId());
            throw new RuntimeException("Invalid credentials");
        }
        
        // Generate JWT token
        String token = jwtUtil.generateToken(user.getUserId());
        
        log.info("Successfully generated token for userId: {}", loginRequest.getUserId());
        
        return new LoginResponse(
            token,
            user.getUserId(),
            "Login successful",
            jwtUtil.getExpirationDateTimeFromToken(token)
        );
    }
    
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }
    
    public String getUserIdFromToken(String token) {
        return jwtUtil.getUserIdFromToken(token);
    }
    
    // Method to create a demo user for testing
    public User createDemoUser(String userId, String password, String fullName) {
        if (userRepository.existsByUserId(userId)) {
            log.warn("User already exists: {}", userId);
            throw new RuntimeException("User already exists");
        }
        
        User user = new User();
        user.setUserId(userId);
        user.setPassword(passwordEncoder.encode(password));
        user.setFullName(fullName);
        user.setActive(true);
        
        User savedUser = userRepository.save(user);
        log.info("Demo user created: {}", userId);
        
        return savedUser;
    }
}