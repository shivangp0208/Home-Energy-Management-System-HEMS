package com.project.hems.Payment_Service.repository;

import com.project.hems.Payment_Service.entity.EnergyRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EnergyRateRepository extends JpaRepository<EnergyRate, Long> {
    
    @Query("SELECT e FROM EnergyRate e WHERE e.isActive = true AND " +
           "e.effectiveFrom <= ?1 AND (e.effectiveTo IS NULL OR e.effectiveTo > ?1) " +
           "ORDER BY e.effectiveFrom DESC LIMIT 1")
    Optional<EnergyRate> findActiveRateAtTime(LocalDateTime dateTime);
    
    @Query("SELECT e FROM EnergyRate e WHERE e.isActive = true ORDER BY e.effectiveFrom DESC LIMIT 1")
    Optional<EnergyRate> findLatestActiveRate();
    
    @Query("SELECT e FROM EnergyRate e WHERE e.isActive = true ORDER BY e.effectiveFrom DESC")
    java.util.List<EnergyRate> findAllActiveRates();
}
