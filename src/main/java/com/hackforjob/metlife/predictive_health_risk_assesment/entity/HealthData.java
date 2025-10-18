package com.hackforjob.metlife.predictive_health_risk_assesment.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing Protected Health Information (PHI)
 * This table stores sensitive health data separately for better security and HIPAA compliance
 */
@Entity
@Table(name = "health_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age must be positive")
    @Max(value = 150, message = "Age must be realistic")
    @Column(nullable = false)
    private Integer age;
    
    @NotBlank(message = "Sex is required")
    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "Sex must be MALE, FEMALE, or OTHER")
    @Column(nullable = false)
    private String sex;
    
    @NotNull(message = "Weight is required")
    @DecimalMin(value = "1.0", message = "Weight must be at least 1.0 kg")
    @DecimalMax(value = "500.0", message = "Weight must be at most 500.0 kg")
    @Column(nullable = false)
    private Double weight;
    
    @NotNull(message = "Height is required")
    @DecimalMin(value = "0.5", message = "Height must be at least 0.5 meters")
    @DecimalMax(value = "3.0", message = "Height must be at most 3.0 meters")
    @Column(nullable = false)
    private Double height;
    
    @NotNull(message = "BMI is required")
    @DecimalMin(value = "10.0", message = "BMI must be at least 10.0")
    @DecimalMax(value = "50.0", message = "BMI must be at most 50.0")
    @Column(nullable = false)
    private Double bmi;
    
    @Column(name = "hereditary_diseases")
    private String hereditaryDiseases;
    
    @NotNull(message = "Number of dependents is required")
    @Min(value = 0, message = "Number of dependents cannot be negative")
    @Column(name = "number_of_dependents", nullable = false)
    private Integer numberOfDependents;
    
    @NotNull(message = "Smoker status is required")
    @Column(name = "is_smoker", nullable = false)
    private Boolean isSmoker;
    
    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City name must not exceed 100 characters")
    @Column(nullable = false)
    private String city;
    
    @NotBlank(message = "Blood pressure status is required")
    @Pattern(regexp = "^(NORMAL|HIGH|LOW|HYPERTENSION_STAGE_1|HYPERTENSION_STAGE_2)$", 
             message = "Blood pressure must be NORMAL, HIGH, LOW, HYPERTENSION_STAGE_1, or HYPERTENSION_STAGE_2")
    @Column(name = "blood_pressure", nullable = false)
    private String bloodPressure;
    
    @NotBlank(message = "Diabetes status is required")
    @Pattern(regexp = "^(Yes|No|yes|no)$", message = "Diabetes status must be Yes or No")
    @Column(name = "has_diabetes", nullable = false)
    private String hasDiabetes;
    
    @NotBlank(message = "Regular exercise status is required")
    @Pattern(regexp = "^(yes|no|Yes|No)$", message = "Regular exercise status must be yes or no")
    @Column(name = "regular_exercise", nullable = false)
    private String regularExercise;
    
    @NotBlank(message = "Job title is required")
    @Size(max = 100, message = "Job title must not exceed 100 characters")
    @Column(name = "job_title", nullable = false)
    private String jobTitle;
    
    // User relationship - Many health data records can belong to one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_health_data_user_id"))
    private User user;
    
    // One-to-one relationship with risk assessment
    @OneToOne(mappedBy = "healthData", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private HealthRiskAssessment healthRiskAssessment;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
