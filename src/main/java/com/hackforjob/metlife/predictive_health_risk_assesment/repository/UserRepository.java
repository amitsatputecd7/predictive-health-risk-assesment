package com.hackforjob.metlife.predictive_health_risk_assesment.repository;

import com.hackforjob.metlife.predictive_health_risk_assesment.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUserId(String userId);
    
    Optional<User> findByUserIdAndActive(String userId, Boolean active);
    
    boolean existsByUserId(String userId);
}