package com.project.hems.payment_service;

import org.springframework.boot.SpringApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Payment Service Application
 * 
 * Handles all billing, rate management, and transaction tracking for the HEMS system.
 * 
 * Features:
 * - Energy rate configuration and management
 * - Daily meter reading processing
 * - Monthly billing calculation and aggregation
 * - Transaction audit logging
 * - Payment tracking and status management
 * - Automatic monthly billing scheduler
 * 
 * Integration:
 * - Consumes MeterSnapshot events from Simulator Service via Kafka
 * - Calls Site Manager Service for site and owner details
 * - Provides billing APIs for Admin and User services
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
@EnableTransactionManagement
public class PaymentServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }
}
