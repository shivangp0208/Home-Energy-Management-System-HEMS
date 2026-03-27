package com.project.hems.site_manager_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI myCustomerConfig() {

        Server localServer = new Server()
                .url("http://localhost:8081")
                .description("Local");

        Server liveServer = new Server()
                .url("http://localhost:8082")
                .description("Live");

        return new OpenAPI()
                .info(new Info()
                        .title("SiteManager microservice APIs")
                        .description("By Jills & Shivange"))
                .servers(List.of(localServer, liveServer));
    }
}
