package com.project.hems.simulator_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableFeignClients
@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableTransactionManagement
public class SimulatorServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimulatorServiceApplication.class, args);
	}

}
