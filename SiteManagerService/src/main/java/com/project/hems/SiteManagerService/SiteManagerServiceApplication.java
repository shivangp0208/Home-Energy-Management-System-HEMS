package com.project.hems.SiteManagerService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@EnableTransactionManagement
public class SiteManagerServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(SiteManagerServiceApplication.class, args);
	}
}

