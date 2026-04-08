package com.project.hems.site_manager_service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@EnableAsync
@SpringBootApplication
@EnableTransactionManagement
@EnableFeignClients
public class site_manager_serviceApplication {
	public static void main(String[] args) {
		SpringApplication.run(site_manager_serviceApplication.class, args);
	}
	@Bean
	public CommandLineRunner logEndpoints(
			@Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping handlerMapping) {

		return args -> {
			System.out.println("====== LIST OF ALL API ENDPOINTS ======");

			handlerMapping.getHandlerMethods().forEach((info, method) -> {

				// NEW WAY (Spring Boot 3+)
				if (info.getPathPatternsCondition() != null) {
					info.getPathPatternsCondition().getPatternValues().forEach(pattern -> {
						info.getMethodsCondition().getMethods().forEach(methodType -> {
							System.out.println(methodType + " -> " + pattern);
						});
					});
				}

			});

			System.out.println("=======================================");
		};
	}
}
