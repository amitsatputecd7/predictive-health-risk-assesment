package com.hackforjob.metlife.predictive_health_risk_assesment.repository;

import com.hackforjob.metlife.predictive_health_risk_assesment.entity.HealthData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HealthDataRepository extends JpaRepository<HealthData, Long> {
    
    /**
     * Find all health data records for a specific user
     */
    List<HealthData> findByUserId(Long userId);
}
