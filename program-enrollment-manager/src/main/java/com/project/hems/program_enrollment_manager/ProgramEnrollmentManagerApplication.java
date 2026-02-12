package com.project.hems.program_enrollment_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
@EnableFeignClients
public class ProgramEnrollmentManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProgramEnrollmentManagerApplication.class, args);
	}

}
