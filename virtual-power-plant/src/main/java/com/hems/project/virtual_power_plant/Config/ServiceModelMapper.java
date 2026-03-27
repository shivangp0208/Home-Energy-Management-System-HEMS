package com.hems.project.virtual_power_plant.Config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceModelMapper {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Global configuration
        modelMapper.getConfiguration()
                .setSkipNullEnabled(true)
                .setAmbiguityIgnored(true);

        return modelMapper;
    }
}
