package com.project.hems.auth_service_hems.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI swaggerConfiguration(){

        Server localServer=new Server()
                .url("http://localhost:8082")
                .description("Local");

        return new OpenAPI()
                .info(new Info()
                        .title("Auth Manager Microservice APIs")
                        .description("by Jills & Shivang"))
                .servers(List.of(localServer));

    }

}
