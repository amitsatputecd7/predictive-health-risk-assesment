package com.hackforjob.metlife.predictive_health_risk_assesment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthRiskAssessmentResponse {
    
    private Long id;
    private Integer age;
    private String sex;
    private Double bmi;
    private String hereditaryDiseases;
    private Integer numberOfDependents;
    private Boolean isSmoker;
    private String city;
    private String bloodPressure;
    private Boolean hasDiabetes;
    private Boolean regularExercise;
    private String jobTitle;
    private Double riskScore;
    private String riskCategory;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}