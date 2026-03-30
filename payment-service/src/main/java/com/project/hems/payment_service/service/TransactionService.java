package com.project.hems.Payment_Service.service;

import com.project.hems.Payment_Service.entity.Transaction;
import com.project.hems.Payment_Service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    
    /**
     * Record a new transaction
     */
    @Transactional
    public Transaction recordTransaction(Transaction transaction) {
        log.info("Recording transaction: owner={}, type={}, amount=${}", 
                 transaction.getOwnerId(), 
                 transaction.getTransactionType(), 
                 transaction.getAmount());
        return transactionRepository.save(transaction);
    }
    
    /**
     * Get all transactions for an owner
     */
    public List<Transaction> getOwnerTransactions(Long ownerId) {
        return transactionRepository.findByOwnerOrderByDateDesc(ownerId);
    }
    
    /**
     * Get transactions for a site
     */
    public List<Transaction> getSiteTransactions(Long siteId) {
        return transactionRepository.findBySiteOrderByDateDesc(siteId);
    }
    
    /**
     * Get transactions for a specific billing period
     */
    public List<Transaction> getBillingTransactions(Long billingId) {
        return transactionRepository.findByBillingId(billingId);
    }
    
    /**
     * Get transactions within a date range
     */
    public List<Transaction> getTransactionsByDateRange(Long ownerId, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByOwnerAndDateRange(ownerId, startDate, endDate);
    }
    
    /**
     * Get transactions of a specific type for an owner
     */
    public List<Transaction> getTransactionsByType(Long ownerId, Transaction.TransactionType type) {
        return transactionRepository.findByOwnerAndType(ownerId, type);
    }
    
    /**
     * Find transaction by reference number
     */
    public Transaction findByReferenceNumber(String referenceNumber) {
        return transactionRepository.findByReferenceNumber(referenceNumber)
            .orElseThrow(() -> new RuntimeException("Transaction not found: " + referenceNumber));
    }
    
    /**
     * Record a payment transaction
     */
    @Transactional
    public Transaction recordPayment(Long ownerId, Long siteId, Long billingId, 
                                     java.math.BigDecimal amount, String referenceNumber) {
        Transaction payment = new Transaction();
        payment.setOwnerId(ownerId);
        payment.setSiteId(siteId);
        payment.setBillingId(billingId);
        payment.setTransactionType(Transaction.TransactionType.PAYMENT);
        payment.setAmount(amount);
        payment.setReferenceNumber(referenceNumber);
        payment.setDescription("User payment towards bill");
        payment.setStatus(Transaction.TransactionStatus.COMPLETED);
        
        log.info("Recording payment: owner={}, amount=${}, reference={}", 
                 ownerId, amount, referenceNumber);
        
        return transactionRepository.save(payment);
    }
}
