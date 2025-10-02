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
}