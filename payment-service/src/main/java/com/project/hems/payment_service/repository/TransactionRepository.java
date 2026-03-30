package com.project.hems.Payment_Service.repository;

import com.project.hems.Payment_Service.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    @Query("SELECT t FROM Transaction t WHERE t.ownerId = ?1 ORDER BY t.transactionDate DESC")
    List<Transaction> findByOwnerOrderByDateDesc(Long ownerId);
    
    @Query("SELECT t FROM Transaction t WHERE t.siteId = ?1 ORDER BY t.transactionDate DESC")
    List<Transaction> findBySiteOrderByDateDesc(Long siteId);
    
    @Query("SELECT t FROM Transaction t WHERE t.billingId = ?1 ORDER BY t.transactionDate DESC")
    List<Transaction> findByBillingId(Long billingId);
    
    @Query("SELECT t FROM Transaction t WHERE t.ownerId = ?1 AND " +
           "t.transactionDate BETWEEN ?2 AND ?3 ORDER BY t.transactionDate DESC")
    List<Transaction> findByOwnerAndDateRange(Long ownerId, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT t FROM Transaction t WHERE t.transactionType = ?1 AND " +
           "t.ownerId = ?2 ORDER BY t.transactionDate DESC")
    List<Transaction> findByOwnerAndType(Long ownerId, Transaction.TransactionType type);
    
    @Query("SELECT t FROM Transaction t WHERE t.referenceNumber = ?1")
    java.util.Optional<Transaction> findByReferenceNumber(String referenceNumber);
}
