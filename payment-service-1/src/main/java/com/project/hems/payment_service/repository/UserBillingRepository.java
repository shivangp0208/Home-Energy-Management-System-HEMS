package com.project.hems.payment_service.repository;

import com.project.hems.payment_service.entity.UserBilling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserBillingRepository extends JpaRepository<UserBilling, Long> {
    
    @Query("SELECT u FROM UserBilling u WHERE u.ownerId = ?1 AND u.billingMonth = ?2")
    Optional<UserBilling> findByOwnerAndMonth(Long ownerId, YearMonth month);
    
    @Query("SELECT u FROM UserBilling u WHERE u.siteId = ?1 AND u.billingMonth = ?2")
    Optional<UserBilling> findBySiteAndMonth(Long siteId, YearMonth month);
    
    @Query("SELECT u FROM UserBilling u WHERE u.ownerId = ?1 ORDER BY u.billingMonth DESC")
    List<UserBilling> findByOwnerOrderByMonthDesc(Long ownerId);
    
    @Query("SELECT u FROM UserBilling u WHERE u.siteId = ?1 ORDER BY u.billingMonth DESC")
    List<UserBilling> findBySiteOrderByMonthDesc(Long siteId);
    
    @Query("SELECT u FROM UserBilling u WHERE u.ownerId = ?1 AND u.invoiceSent = false")
    List<UserBilling> findPendingInvoicesByOwner(Long ownerId);
    
    @Query("SELECT u FROM UserBilling u WHERE u.paymentStatus = com.project.hems.payment_service.entity.UserBilling$PaymentStatus.PENDING " +
           "ORDER BY u.billingMonth DESC")
    List<UserBilling> findPendingPayments();
    
    @Query("SELECT u FROM UserBilling u WHERE u.paymentStatus = com.project.hems.payment_service.entity.UserBilling$PaymentStatus.OVERDUE")
    List<UserBilling> findOverduePayments();
}
