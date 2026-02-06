package com.project.hems.program_enrollment_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class ProgramEnrollmentManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProgramEnrollmentManagerApplication.class, args);
	}

	@PostConstruct 
public void started() {
  System.out.println("✅ PROGRAM-ENROLLMENT-MANAGER STARTED");
}


}
