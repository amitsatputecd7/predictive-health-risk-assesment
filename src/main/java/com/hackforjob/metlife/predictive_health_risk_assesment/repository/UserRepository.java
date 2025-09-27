package com.hackforjob.metlife.predictive_health_risk_assesment.repository;

import com.hackforjob.metlife.predictive_health_risk_assesment.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmailAndId(String email, Long id);
    
    boolean existsByEmail(String email);
    
    boolean existsByUsername(String username);
}