package com.hems.project.Virtual_Power_Plant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class VirtualPowerPlantApplication {

	public static void main(String[] args) {
		SpringApplication.run(VirtualPowerPlantApplication.class, args);
	}

}

