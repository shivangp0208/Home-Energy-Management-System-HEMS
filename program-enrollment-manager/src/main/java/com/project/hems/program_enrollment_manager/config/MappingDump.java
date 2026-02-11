package com.project.hems.program_enrollment_manager.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

// @Configuration
public class MappingDump {

  @Bean
  ApplicationRunner dumpMappings(RequestMappingHandlerMapping mapping) {
    return args -> mapping.getHandlerMethods().forEach((info, method) -> {
      var patterns = info.getPathPatternsCondition() != null
          ? info.getPathPatternsCondition().toString()
          : info.getPatternsCondition().toString();

      if (patterns.contains("/api/v1/program")) {
        System.out.println("MAPPED: " + patterns + " -> " + method);
      }
    });
  }
}
