package com.project.hems.payment_service.repository;

import com.project.hems.payment_service.entity.MeterReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
public interface MeterReadingRepository extends JpaRepository<MeterReading, Long> {
    
    @Query("SELECT m FROM MeterReading m WHERE m.siteId = ?1 AND m.readingDate = ?2")
    Optional<MeterReading> findByDateAndSite(Long siteId, LocalDate date);
    
    @Query("SELECT m FROM MeterReading m WHERE m.siteId = ?1 AND " +
           "YEAR(m.readingDate) = ?2 AND MONTH(m.readingDate) = ?3 " +
           "ORDER BY m.readingDate ASC")
    List<MeterReading> findByMonthAndSite(Long siteId, int year, int month);
    
    @Query("SELECT m FROM MeterReading m WHERE m.ownerId = ?1 AND " +
           "YEAR(m.readingDate) = ?2 AND MONTH(m.readingDate) = ?3 " +
           "ORDER BY m.readingDate ASC")
    List<MeterReading> findByMonthAndOwner(Long ownerId, int year, int month);
    
    @Query("SELECT m FROM MeterReading m WHERE m.siteId = ?1 AND " +
           "m.readingDate BETWEEN ?2 AND ?3 ORDER BY m.readingDate ASC")
    List<MeterReading> findByDateRange(Long siteId, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT m FROM MeterReading m WHERE m.processed = false ORDER BY m.readingDate ASC")
    List<MeterReading> findUnprocessedReadings();
    
    @Query("SELECT m FROM MeterReading m WHERE m.siteId = ?1 AND m.processed = false")
    List<MeterReading> findUnprocessedReadingsBySite(Long siteId);
}
