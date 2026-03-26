package com.project.hems.service_registry_hems;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class ServiceRegistryHemsApplication {
	public static void main(String[] args) {
		SpringApplication.run(ServiceRegistryHemsApplication.class, args);
	}
}

