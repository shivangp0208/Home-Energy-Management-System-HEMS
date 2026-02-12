package com.hems.project.Email_Service.config;

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
                .url("http://localhost:8088")
                .description("Local");

        return new OpenAPI()
                .info(new Info()
                        .title("Email Microservice APIs")
                        .description("by Jills & Shivang"))
                .servers(List.of(localServer));

    }

}
