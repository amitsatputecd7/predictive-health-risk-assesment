package com.hackforjob.metlife.predictive_health_risk_assesment.repository;

import com.hackforjob.metlife.predictive_health_risk_assesment.entity.HealthRiskAssessment;
import com.hackforjob.metlife.predictive_health_risk_assesment.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HealthRiskAssessmentRepository extends JpaRepository<HealthRiskAssessment, Long> {
    
    // Find assessments by user
    List<HealthRiskAssessment> findByUser(User user);
    
    // Find assessments by user ID
    List<HealthRiskAssessment> findByUserId(Long userId);
    
    // Find assessments by risk category
    List<HealthRiskAssessment> findByRiskCategory(String riskCategory);
    
    // Find high-risk assessments (risk score > 70)
    @Query("SELECT h FROM HealthRiskAssessment h WHERE h.riskScore > :riskScore")
    List<HealthRiskAssessment> findHighRiskAssessments(@Param("riskScore") Double riskScore);
    
    // Count assessments by risk category
    @Query("SELECT h.riskCategory, COUNT(h) FROM HealthRiskAssessment h GROUP BY h.riskCategory")
    List<Object[]> countByRiskCategory();
}