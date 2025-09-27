package com.hackforjob.metlife.predictive_health_risk_assesment.service;

import com.hackforjob.metlife.predictive_health_risk_assesment.dto.HealthRiskAssessmentRequest;
import com.hackforjob.metlife.predictive_health_risk_assesment.dto.HealthRiskAssessmentResponse;
import com.hackforjob.metlife.predictive_health_risk_assesment.entity.HealthRiskAssessment;
import com.hackforjob.metlife.predictive_health_risk_assesment.repository.HealthRiskAssessmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class HealthRiskAssessmentService {
    
    private final HealthRiskAssessmentRepository repository;
    
    public HealthRiskAssessmentResponse createAssessment(HealthRiskAssessmentRequest request) {
        log.info("Creating health risk assessment for age: {}, city: {}", request.getAge(), request.getCity());
        
        HealthRiskAssessment assessment = mapToEntity(request);
        
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
        if (assessment.getHereditaryDiseases() != null && !assessment.getHereditaryDiseases().trim().isEmpty()) {
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
    
    private HealthRiskAssessment mapToEntity(HealthRiskAssessmentRequest request) {
        HealthRiskAssessment assessment = new HealthRiskAssessment();
        updateEntityFromRequest(assessment, request);
        return assessment;
    }
    
    private void updateEntityFromRequest(HealthRiskAssessment assessment, HealthRiskAssessmentRequest request) {
        assessment.setAge(request.getAge());
        assessment.setSex(request.getSex());
        assessment.setWeight(request.getWeight());
        assessment.setBmi(request.getBmi());
        assessment.setHereditaryDiseases(request.getHereditaryDiseases());
        assessment.setNumberOfDependents(request.getNumberOfDependents());
        assessment.setIsSmoker(request.getIsSmoker());
        assessment.setCity(request.getCity());
        assessment.setBloodPressure(request.getBloodPressure());
        assessment.setHasDiabetes(request.getHasDiabetes());
        assessment.setRegularExercise(request.getRegularExercise());
        assessment.setJobTitle(request.getJobTitle());
    }
    
    private HealthRiskAssessmentResponse mapToResponse(HealthRiskAssessment assessment) {
        return new HealthRiskAssessmentResponse(
                assessment.getId(),
                assessment.getAge(),
                assessment.getSex(),
                assessment.getWeight(),
                assessment.getBmi(),
                assessment.getHereditaryDiseases(),
                assessment.getNumberOfDependents(),
                assessment.getIsSmoker(),
                assessment.getCity(),
                assessment.getBloodPressure(),
                assessment.getHasDiabetes(),
                assessment.getRegularExercise(),
                assessment.getJobTitle(),
                assessment.getRiskScore(),
                assessment.getRiskCategory(),
                assessment.getCreatedAt(),
                assessment.getUpdatedAt()
        );
    }
}