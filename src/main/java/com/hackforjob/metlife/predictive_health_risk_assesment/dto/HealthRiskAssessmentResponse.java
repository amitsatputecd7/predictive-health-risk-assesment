package com.hackforjob.metlife.predictive_health_risk_assesment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthRiskAssessmentResponse {
    
    private Long id;
    private Integer age;
    private String sex;
    private Double weight;
    private Double bmi;
    @JsonProperty("hereditary_diseases")
    private String hereditaryDiseases;
    @JsonProperty("number_of_dependents")
    private Integer numberOfDependents;
    @JsonProperty("is_smoker")
    private Boolean isSmoker;
    private String city;
    @JsonProperty("blood_pressure")
    private String bloodPressure;
    @JsonProperty("has_diabetes")
    private String hasDiabetes;
    @JsonProperty("regular_exercise")
    private String regularExercise;
    @JsonProperty("job_title")
    private String jobTitle;
    @JsonProperty("risk_score")
    private Double riskScore;
    @JsonProperty("risk_category")
    private String riskCategory;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    
    private Integer score;
    private List<String> suggestions;
}