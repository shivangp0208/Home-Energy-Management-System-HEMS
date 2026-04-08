package com.hems.project.admin_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class AdminServiceApplication {

	public static void main(String[] args) {
		System.setProperty("spring.application.name", "ADMIN-SERVICE");
		System.setProperty("server.port", "8093");
		SpringApplication.run(AdminServiceApplication.class, args);
	}
}