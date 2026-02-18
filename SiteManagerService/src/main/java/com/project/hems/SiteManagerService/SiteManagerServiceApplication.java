package com.project.hems.SiteManagerService;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.PostMapping;


@EnableFeignClients
@EnableAsync
@SpringBootApplication
@EnableTransactionManagement
public class SiteManagerServiceApplication {
	@Value("${einfochips.key}")
	public String key;

	@PostConstruct
	public void postConstruct(){
		System.out.println("keyssssssss"+key);
	}
	public static void main(String[] args) {
		SpringApplication.run(SiteManagerServiceApplication.class, args);
	}
}

