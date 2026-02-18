package com.project.hems.SiteManagerService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

// TODO: add feign client over here and then in external define it
@EnableAsync
@SpringBootApplication
@EnableTransactionManagement
@EnableFeignClients
public class SiteManagerServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(SiteManagerServiceApplication.class, args);
	}
}

