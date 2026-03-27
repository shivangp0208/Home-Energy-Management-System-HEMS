package com.project.hems.program_enrollment_manager;

import com.hems.ExcelModule.config.ExcelAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableFeignClients
@Import(ExcelAutoConfiguration.class)
public class ProgramEnrollmentManagerApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProgramEnrollmentManagerApplication.class, args);
	}

}
