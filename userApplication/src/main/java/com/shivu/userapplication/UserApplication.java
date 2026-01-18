package com.shivu.userapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);
	}

	// CommandLineRunner disabled - using persistent MySQL database
	// Data initialization is not needed on every startup
	// Use RBACService.initialize() only for fresh database setup
}
