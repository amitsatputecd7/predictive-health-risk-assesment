package com.hackforjob.metlife.predictive_health_risk_assesment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    
    @NotNull(message = "Weight is required")
    @DecimalMin(value = "1.0", message = "Weight must be at least 1.0 kg")
    @DecimalMax(value = "500.0", message = "Weight must be at most 500.0 kg")
    private Double weight;
    
    @NotNull(message = "Height is required")
    @DecimalMin(value = "0.5", message = "Height must be at least 0.5 meters")
    @DecimalMax(value = "3.0", message = "Height must be at most 3.0 meters")
    private Double height;
    
    @NotNull(message = "BMI is required")
    @DecimalMin(value = "10.0", message = "BMI must be at least 10.0")
    @DecimalMax(value = "50.0", message = "BMI must be at most 50.0")
    private Double bmi;
    
    @JsonProperty("hereditary_diseases")
    private List<String> hereditaryDiseases;
    
    @NotNull(message = "Number of dependents is required")
    @Min(value = 0, message = "Number of dependents cannot be negative")
    @JsonProperty("number_of_dependents")
    private Integer numberOfDependents;
    
    @NotNull(message = "Smoker status is required")
    @JsonProperty("is_smoker")
    private Boolean isSmoker;
    
    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City name must not exceed 100 characters")
    private String city;
    
    @NotBlank(message = "Blood pressure status is required")
    @Pattern(regexp = "^(NORMAL|HIGH|LOW|HYPERTENSION_STAGE_1|HYPERTENSION_STAGE_2)$", 
             message = "Blood pressure must be NORMAL, HIGH, LOW, HYPERTENSION_STAGE_1, or HYPERTENSION_STAGE_2")
    @JsonProperty("blood_pressure")
    private String bloodPressure;
    
    @NotBlank(message = "Diabetes status is required")
    @Pattern(regexp = "^(Yes|No|yes|no)$", message = "Diabetes status must be Yes or No")
    @JsonProperty("has_diabetes")
    private String hasDiabetes;
    
    @NotBlank(message = "Regular exercise status is required")
    @Pattern(regexp = "^(yes|no|Yes|No)$", message = "Regular exercise status must be yes or no")
    @JsonProperty("regular_exercise")
    private String regularExercise;
    
    @NotBlank(message = "Job title is required")
    @Size(max = 100, message = "Job title must not exceed 100 characters")
    @JsonProperty("job_title")
    private String jobTitle;
    
    @JsonProperty("userId")
    private Long userId;
}