package com.hackforjob.metlife.predictive_health_risk_assesment.service;

import com.hackforjob.metlife.predictive_health_risk_assesment.dto.LoginRequest;
import com.hackforjob.metlife.predictive_health_risk_assesment.dto.LoginResponse;
import com.hackforjob.metlife.predictive_health_risk_assesment.dto.UserRegistrationRequest;
import com.hackforjob.metlife.predictive_health_risk_assesment.entity.User;
import com.hackforjob.metlife.predictive_health_risk_assesment.repository.UserRepository;
import com.hackforjob.metlife.predictive_health_risk_assesment.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("Login attempt for email: {}", loginRequest.getEmail());
        
        // Check if user exists with the provided email
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
        
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            log.info("Existing user found: {}", user.getUsername());
        } else {
            // Create new user since they don't exist
            String username = generateUsernameFromEmail(loginRequest.getEmail());
            user = new User(loginRequest.getEmail(), username);
            user = userRepository.save(user);
            log.info("New user created: {}", user.getUsername());
        }
        
        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getUsername());
        
        return new LoginResponse(
            token,
            user.getId(),
            user.getEmail(),
            user.getUsername(),
            "Login successful"
        );
    }
    
    @Transactional
    public User registerUser(UserRegistrationRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());
        
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User already exists with email: " + request.getEmail());
        }
        
        // Generate username from email
        String username = generateUsernameFromEmail(request.getEmail());
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(username);
        user.setFirstName(request.getFirstName());
        user.setIsActive(true);
        
        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getUsername());
        
        return savedUser;
    }
    
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }
    
    public boolean validateToken(String token, String email) {
        return jwtUtil.validateToken(token, email);
    }
    
    public String getEmailFromToken(String token) {
        return jwtUtil.getEmailFromToken(token);
    }
    
    public Long getUserIdFromToken(String token) {
        return jwtUtil.getUserIdFromToken(token);
    }
    
    private String generateUsernameFromEmail(String email) {
        String username = email.split("@")[0];
        // Add random suffix if username already exists
        String originalUsername = username;
        int counter = 1;
        while (userRepository.existsByUsername(username)) {
            username = originalUsername + counter;
            counter++;
        }
        return username;
    }
}