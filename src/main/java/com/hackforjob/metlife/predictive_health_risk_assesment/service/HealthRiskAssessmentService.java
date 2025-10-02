package com.hackforjob.metlife.predictive_health_risk_assesment.service;

import com.hackforjob.metlife.predictive_health_risk_assesment.dto.HealthRiskAssessmentRequest;
import com.hackforjob.metlife.predictive_health_risk_assesment.dto.HealthRiskAssessmentResponse;
import com.hackforjob.metlife.predictive_health_risk_assesment.entity.HealthRiskAssessment;
import com.hackforjob.metlife.predictive_health_risk_assesment.entity.User;
import com.hackforjob.metlife.predictive_health_risk_assesment.repository.HealthRiskAssessmentRepository;
import com.hackforjob.metlife.predictive_health_risk_assesment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class HealthRiskAssessmentService {
    
    private final HealthRiskAssessmentRepository repository;
    private final UserRepository userRepository;
    private final Random random = new Random();
    
    public HealthRiskAssessmentResponse createAssessment(HealthRiskAssessmentRequest request) {
        log.info("Creating health risk assessment for age: {}, city: {}, userId: {}", 
                request.getAge(), request.getCity(), request.getUserId());
        
        HealthRiskAssessment assessment = mapToEntity(request);
        
        // Associate with user if userId is provided
        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));
            assessment.setUser(user);
            log.info("Associated assessment with user: {}", user.getUsername());
        }
        
        // Calculate risk score and category
        calculateRiskScore(assessment);
        
        HealthRiskAssessment savedAssessment = repository.save(assessment);
        
        log.info("Created assessment with ID: {} and risk score: {}", 
                savedAssessment.getId(), savedAssessment.getRiskScore());
        
        return mapToResponse(savedAssessment);
    }
    
    @Transactional(readOnly = true)
    public List<HealthRiskAssessmentResponse> getAllAssessments() {
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Optional<HealthRiskAssessmentResponse> getAssessmentById(Long id) {
        return repository.findById(id)
                .map(this::mapToResponse);
    }
    
    public HealthRiskAssessmentResponse updateAssessment(Long id, HealthRiskAssessmentRequest request) {
        HealthRiskAssessment existingAssessment = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assessment not found with id: " + id));
        
        updateEntityFromRequest(existingAssessment, request);
        calculateRiskScore(existingAssessment);
        
        HealthRiskAssessment updatedAssessment = repository.save(existingAssessment);
        
        log.info("Updated assessment with ID: {} and new risk score: {}", 
                updatedAssessment.getId(), updatedAssessment.getRiskScore());
        
        return mapToResponse(updatedAssessment);
    }
    
    public void deleteAssessment(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Assessment not found with id: " + id);
        }
        repository.deleteById(id);
        log.info("Deleted assessment with ID: {}", id);
    }
    
    @Transactional(readOnly = true)
    public List<HealthRiskAssessmentResponse> getAssessmentsByCity(String city) {
        return repository.findByCity(city).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<HealthRiskAssessmentResponse> getAssessmentsByRiskCategory(String riskCategory) {
        return repository.findByRiskCategory(riskCategory).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<HealthRiskAssessmentResponse> getHighRiskAssessments() {
        return repository.findHighRiskAssessments(70.0).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<HealthRiskAssessmentResponse> getAssessmentsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        return repository.findByUser(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    private void calculateRiskScore(HealthRiskAssessment assessment) {
        double riskScore = 0.0;
        
        // Age factor (0-25 points)
        if (assessment.getAge() < 30) {
            riskScore += 5;
        } else if (assessment.getAge() < 50) {
            riskScore += 10;
        } else if (assessment.getAge() < 65) {
            riskScore += 20;
        } else {
            riskScore += 25;
        }
        
        // BMI factor (0-20 points)
        if (assessment.getBmi() < 18.5 || assessment.getBmi() > 30) {
            riskScore += 20;
        } else if (assessment.getBmi() > 25) {
            riskScore += 10;
        } else {
            riskScore += 5;
        }
        
        // Smoking factor (0-25 points)
        if (assessment.getIsSmoker()) {
            riskScore += 25;
        }
        
        // Diabetes factor (0-20 points)
        if ("Yes".equalsIgnoreCase(assessment.getHasDiabetes())) {
            riskScore += 20;
        }
        
        // Blood pressure factor (0-15 points)
        switch (assessment.getBloodPressure()) {
            case "HYPERTENSION_STAGE_2":
                riskScore += 15;
                break;
            case "HYPERTENSION_STAGE_1":
                riskScore += 12;
                break;
            case "HIGH":
                riskScore += 8;
                break;
            case "LOW":
                riskScore += 5;
                break;
            case "NORMAL":
            default:
                riskScore += 2;
                break;
        }
        
        // Exercise factor (0-10 points, inverted - no exercise increases risk)
        if ("no".equalsIgnoreCase(assessment.getRegularExercise())) {
            riskScore += 10;
        }
        
        // Hereditary diseases factor (0-10 points)
        if (assessment.getHereditaryDiseases() != null && 
            !assessment.getHereditaryDiseases().trim().isEmpty() && 
            !assessment.getHereditaryDiseases().equalsIgnoreCase("None")) {
            riskScore += 10;
        }
        
        // Dependents factor (slight risk increase with more dependents due to stress)
        if (assessment.getNumberOfDependents() > 3) {
            riskScore += 5;
        }
        
        // Ensure score is within 0-100 range
        riskScore = Math.min(100.0, Math.max(0.0, riskScore));
        
        assessment.setRiskScore(riskScore);
        assessment.setRiskCategory(determineRiskCategory(riskScore));
    }
    
    private String determineRiskCategory(double riskScore) {
        if (riskScore < 30) {
            return "LOW";
        } else if (riskScore < 70) {
            return "MEDIUM";
        } else {
            return "HIGH";
        }
    }
    
    private List<String> generateRandomSuggestions() {
        List<String> allSuggestions = Arrays.asList(
            "You should jog regularly for better cardiovascular health",
            "Consider walking at least 30 minutes daily",
            "Regular exercise can significantly improve your health",
            "Try swimming as a low-impact exercise option",
            "Yoga and meditation can help reduce stress",
            "Maintain a balanced diet with plenty of vegetables",
            "Drink more water throughout the day",
            "Get adequate sleep of 7-8 hours daily",
            "Consider cycling as a fun way to stay active",
            "Regular health check-ups are important",
            "Limit processed foods and sugar intake",
            "Practice deep breathing exercises"
        );
        
        // Return 2-4 random suggestions
        int numSuggestions = 2 + random.nextInt(3); // 2, 3, or 4 suggestions
        return allSuggestions.stream()
                .sorted((a, b) -> random.nextInt(3) - 1)
                .limit(numSuggestions)
                .collect(Collectors.toList());
    }
    
    private HealthRiskAssessment mapToEntity(HealthRiskAssessmentRequest request) {
        HealthRiskAssessment assessment = new HealthRiskAssessment();
        updateEntityFromRequest(assessment, request);
        return assessment;
    }
    
    private void updateEntityFromRequest(HealthRiskAssessment assessment, HealthRiskAssessmentRequest request) {
        assessment.setAge(request.getAge());
        assessment.setSex(request.getSex());
        assessment.setWeight(request.getWeight());
        assessment.setHeight(request.getHeight());
        assessment.setBmi(request.getBmi());
        
        // Convert List<String> to comma-separated String for database storage
        if (request.getHereditaryDiseases() != null && !request.getHereditaryDiseases().isEmpty()) {
            assessment.setHereditaryDiseases(String.join(",", request.getHereditaryDiseases()));
        } else {
            assessment.setHereditaryDiseases(null);
        }
        
        assessment.setNumberOfDependents(request.getNumberOfDependents());
        assessment.setIsSmoker(request.getIsSmoker());
        assessment.setCity(request.getCity());
        assessment.setBloodPressure(request.getBloodPressure());
        assessment.setHasDiabetes(request.getHasDiabetes());
        assessment.setRegularExercise(request.getRegularExercise());
        assessment.setJobTitle(request.getJobTitle());
    }
    
    private HealthRiskAssessmentResponse mapToResponse(HealthRiskAssessment assessment) {
        HealthRiskAssessmentResponse response = new HealthRiskAssessmentResponse();
        response.setId(assessment.getId());
        response.setAge(assessment.getAge());
        response.setSex(assessment.getSex());
        response.setWeight(assessment.getWeight());
        response.setHeight(assessment.getHeight());
        response.setBmi(assessment.getBmi());
        
        // Convert comma-separated String back to List<String> for response
        if (assessment.getHereditaryDiseases() != null && !assessment.getHereditaryDiseases().trim().isEmpty()) {
            response.setHereditaryDiseases(Arrays.asList(assessment.getHereditaryDiseases().split(",")));
        } else {
            response.setHereditaryDiseases(Arrays.asList("None"));
        }
        
        response.setNumberOfDependents(assessment.getNumberOfDependents());
        response.setIsSmoker(assessment.getIsSmoker());
        response.setCity(assessment.getCity());
        response.setBloodPressure(assessment.getBloodPressure());
        response.setHasDiabetes(assessment.getHasDiabetes());
        response.setRegularExercise(assessment.getRegularExercise());
        response.setJobTitle(assessment.getJobTitle());
        response.setRiskScore(assessment.getRiskScore());
        response.setRiskCategory(assessment.getRiskCategory());
        response.setCreatedAt(assessment.getCreatedAt());
        response.setUpdatedAt(assessment.getUpdatedAt());
        
        // Add random score and suggestions for now
        response.setScore(random.nextInt(101)); // Random score 0-100
        response.setSuggestions(generateRandomSuggestions());
        
        return response;
    }
}