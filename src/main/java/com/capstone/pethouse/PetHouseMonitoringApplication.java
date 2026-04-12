package com.capstone.pethouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class PetHouseMonitoringApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetHouseMonitoringApplication.class, args);
	}

}
