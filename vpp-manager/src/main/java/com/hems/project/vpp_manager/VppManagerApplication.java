package com.hems.project.vpp_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class VppManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(VppManagerApplication.class, args);
	}

}
