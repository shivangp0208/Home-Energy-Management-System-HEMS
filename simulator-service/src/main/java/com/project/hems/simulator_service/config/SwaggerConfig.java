package com.project.hems.simulator_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    Server localServer=new Server()
            .url("http://localhost:9010")
            .description("local server");

    @Bean
    public OpenAPI swaggerConfiguration(){
        return new OpenAPI()
                .info(new Info()
                        .title("Simulator service")
                        .description("by Jills & Shivang")
                )
                .servers(List.of(localServer));
    }
}
