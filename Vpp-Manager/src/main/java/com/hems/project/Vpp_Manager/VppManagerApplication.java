package com.hems.project.Vpp_Manager;

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
