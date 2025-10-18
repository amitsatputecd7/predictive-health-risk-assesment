package com.hackforjob.metlife.predictive_health_risk_assesment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing Health Risk Assessment results (Non-PHI data)
 * This table stores only calculated risk scores and categories, not actual health data
 */
@Entity
@Table(name = "health_risk_assessments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthRiskAssessment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Calculated risk score (0-100)
    @Column(name = "risk_score")
    private Double riskScore;
    
    @Column(name = "risk_category")
    private String riskCategory; // LOW, MEDIUM, HIGH
    
    // One-to-one relationship with health data (PHI)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "health_data_id", nullable = false, 
                foreignKey = @ForeignKey(name = "fk_health_risk_health_data_id"))
    private HealthData healthData;
    
    // User relationship - Many assessments can belong to one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_user_id"))
    private User user;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}