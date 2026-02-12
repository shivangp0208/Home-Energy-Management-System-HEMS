package com.project.hems.program_enrollment_manager.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI myCustomerConfig() {

        Server programServer = new Server()
                .url("http://localhost:8089")
                .description("Development");


        return new OpenAPI()
                .info(new Info()
                        .title("Program manager microservice APIs")
                        .description("This Service is built to manage programs and site enrollment in that program"))
                .servers(List.of(programServer));
    }
}