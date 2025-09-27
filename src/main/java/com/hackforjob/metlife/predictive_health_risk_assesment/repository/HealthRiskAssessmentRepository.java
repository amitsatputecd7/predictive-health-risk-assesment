package com.hackforjob.metlife.predictive_health_risk_assesment.repository;

import com.hackforjob.metlife.predictive_health_risk_assesment.entity.HealthRiskAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HealthRiskAssessmentRepository extends JpaRepository<HealthRiskAssessment, Long> {
    
    // Find assessments by city
    List<HealthRiskAssessment> findByCity(String city);
    
    // Find assessments by risk category
    List<HealthRiskAssessment> findByRiskCategory(String riskCategory);
    
    // Find assessments by age range
    List<HealthRiskAssessment> findByAgeBetween(Integer minAge, Integer maxAge);
    
    // Find assessments by job title
    List<HealthRiskAssessment> findByJobTitleContainingIgnoreCase(String jobTitle);
    
    // Find high-risk assessments (risk score > 70)
    @Query("SELECT h FROM HealthRiskAssessment h WHERE h.riskScore > :riskScore")
    List<HealthRiskAssessment> findHighRiskAssessments(@Param("riskScore") Double riskScore);
    
    // Count assessments by risk category
    @Query("SELECT h.riskCategory, COUNT(h) FROM HealthRiskAssessment h GROUP BY h.riskCategory")
    List<Object[]> countByRiskCategory();
    
    // Find assessments by multiple health conditions
    @Query("SELECT h FROM HealthRiskAssessment h WHERE " +
           "(:isSmoker IS NULL OR h.isSmoker = :isSmoker) AND " +
           "(:hasDiabetes IS NULL OR h.hasDiabetes = :hasDiabetes) AND " +
           "(:regularExercise IS NULL OR h.regularExercise = :regularExercise)")
    List<HealthRiskAssessment> findByHealthConditions(
            @Param("isSmoker") Boolean isSmoker,
            @Param("hasDiabetes") Boolean hasDiabetes,
            @Param("regularExercise") Boolean regularExercise
    );
}