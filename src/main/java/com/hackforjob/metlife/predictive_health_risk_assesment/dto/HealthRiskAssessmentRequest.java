package com.hackforjob.metlife.predictive_health_risk_assesment.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthRiskAssessmentRequest {
    
    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age must be positive")
    @Max(value = 150, message = "Age must be realistic")
    private Integer age;
    
    @NotBlank(message = "Sex is required")
    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "Sex must be MALE, FEMALE, or OTHER")
    private String sex;
    
    @NotNull(message = "BMI is required")
    @DecimalMin(value = "10.0", message = "BMI must be at least 10.0")
    @DecimalMax(value = "50.0", message = "BMI must be at most 50.0")
    private Double bmi;
    
    private String hereditaryDiseases;
    
    @NotNull(message = "Number of dependents is required")
    @Min(value = 0, message = "Number of dependents cannot be negative")
    private Integer numberOfDependents;
    
    @NotNull(message = "Smoker status is required")
    private Boolean isSmoker;
    
    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City name must not exceed 100 characters")
    private String city;
    
    @NotBlank(message = "Blood pressure status is required")
    @Pattern(regexp = "^(NORMAL|HIGH|LOW|HYPERTENSION_STAGE_1|HYPERTENSION_STAGE_2)$", 
             message = "Blood pressure must be NORMAL, HIGH, LOW, HYPERTENSION_STAGE_1, or HYPERTENSION_STAGE_2")
    private String bloodPressure;
    
    @NotNull(message = "Diabetes status is required")
    private Boolean hasDiabetes;
    
    @NotNull(message = "Regular exercise status is required")
    private Boolean regularExercise;
    
    @NotBlank(message = "Job title is required")
    @Size(max = 100, message = "Job title must not exceed 100 characters")
    private String jobTitle;
}