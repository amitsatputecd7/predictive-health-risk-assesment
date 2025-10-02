package com.hackforjob.metlife.predictive_health_risk_assesment.controller;

import com.hackforjob.metlife.predictive_health_risk_assesment.dto.HealthRiskAssessmentRequest;
import com.hackforjob.metlife.predictive_health_risk_assesment.dto.HealthRiskAssessmentResponse;
import com.hackforjob.metlife.predictive_health_risk_assesment.service.HealthRiskAssessmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/health-risk-assessment")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class HealthRiskAssessmentController {
    
    private final HealthRiskAssessmentService service;
    
    @PostMapping
    public ResponseEntity<HealthRiskAssessmentResponse> createAssessment(
            @Valid @RequestBody HealthRiskAssessmentRequest request) {
        log.info("Received request to create health risk assessment");
        HealthRiskAssessmentResponse response = service.createAssessment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping
    public ResponseEntity<?> getAllAssessments() {
        log.info("Received request to get all health risk assessments");
        return ResponseEntity.ok(service.getAllAssessments());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getAssessmentById(@PathVariable Long id) {
        log.info("Received request to get health risk assessment with id: {}", id);
        return service.getAssessmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAssessmentsByUserId(@PathVariable Long userId) {
        log.info("Received request to get health risk assessments for user id: {}", userId);
        return ResponseEntity.ok(service.getAssessmentsByUserId(userId));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<HealthRiskAssessmentResponse> updateAssessment(
            @PathVariable Long id,
            @Valid @RequestBody HealthRiskAssessmentRequest request) {
        log.info("Received request to update health risk assessment with id: {}", id);
        HealthRiskAssessmentResponse response = service.updateAssessment(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssessment(@PathVariable Long id) {
        log.info("Received request to delete health risk assessment with id: {}", id);
        service.deleteAssessment(id);
        return ResponseEntity.noContent().build();
    }
}