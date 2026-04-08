package com.project.hems.payment_service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@SpringBootApplication
public class PaymentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentServiceApplication.class, args);
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
