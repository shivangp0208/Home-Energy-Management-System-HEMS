package com.project.hems.site_manager_service.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SiteMapper {

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
}
