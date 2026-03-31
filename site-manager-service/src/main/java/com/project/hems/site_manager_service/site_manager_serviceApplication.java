package com.project.hems.site_manager_service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAsync
@SpringBootApplication
@EnableTransactionManagement
@EnableFeignClients
@Import(com.hems.excel_module.config.ExcelAutoConfiguration.class)
public class site_manager_serviceApplication {
	public static void main(String[] args) {
		SpringApplication.run(site_manager_serviceApplication.class, args);
	}
}
