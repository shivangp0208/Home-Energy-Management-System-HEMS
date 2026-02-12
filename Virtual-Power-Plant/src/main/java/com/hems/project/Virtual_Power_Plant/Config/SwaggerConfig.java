package com.hems.project.Virtual_Power_Plant.Config;

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
                .url("http://localhost:8086")
                .description("Local");

        return new OpenAPI()
                .info(new Info()
                        .title("Virtual Power Plant Microservice APIs")
                        .description("by Jills & Shivang"))
                .servers(List.of(localServer));

    }

}
