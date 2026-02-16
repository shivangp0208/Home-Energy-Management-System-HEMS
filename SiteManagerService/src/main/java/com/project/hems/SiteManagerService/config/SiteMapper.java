package com.project.hems.SiteManagerService.config;

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
