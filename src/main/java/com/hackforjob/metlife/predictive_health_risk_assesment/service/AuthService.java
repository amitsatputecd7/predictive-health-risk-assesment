package com.hackforjob.metlife.predictive_health_risk_assesment.service;

import com.hackforjob.metlife.predictive_health_risk_assesment.dto.LoginRequest;
import com.hackforjob.metlife.predictive_health_risk_assesment.dto.LoginResponse;
import com.hackforjob.metlife.predictive_health_risk_assesment.dto.UserRegistrationRequest;
import com.hackforjob.metlife.predictive_health_risk_assesment.entity.User;
import com.hackforjob.metlife.predictive_health_risk_assesment.repository.UserRepository;
import com.hackforjob.metlife.predictive_health_risk_assesment.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("Login attempt for username: {}", loginRequest.getUsername());
        
        // Find user by username
        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());
        
        if (userOptional.isEmpty()) {
            log.warn("Login failed - user not found: {}", loginRequest.getUsername());
            throw new RuntimeException("Invalid username or password");
        }
        
        User user = userOptional.get();
        
        // Verify password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            log.warn("Login failed - invalid password for user: {}", loginRequest.getUsername());
            throw new RuntimeException("Invalid username or password");
        }
        
        // Check if user is active
        if (!user.getIsActive()) {
            log.warn("Login failed - user account is inactive: {}", loginRequest.getUsername());
            throw new RuntimeException("User account is inactive");
        }
        
        log.info("Login successful for user: {}", user.getUsername());
        
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
        log.info("Registering new user with username: {} and email: {}", request.getUsername(), request.getEmail());
        
        // Check if user already exists by email
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed - user already exists with email: {}", request.getEmail());
            throw new RuntimeException("User already exists with email: " + request.getEmail());
        }
        
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Registration failed - username already exists: {}", request.getUsername());
            throw new RuntimeException("Username already exists: " + request.getUsername());
        }
        
        // Encode password
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        
        // Create new user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(encodedPassword);
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
    

}