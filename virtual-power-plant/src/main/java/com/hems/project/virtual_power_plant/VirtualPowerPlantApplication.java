package com.hems.project.virtual_power_plant;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class VirtualPowerPlantApplication {


	public static void main(String[] args) {
		SpringApplication.run(VirtualPowerPlantApplication.class, args);
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

