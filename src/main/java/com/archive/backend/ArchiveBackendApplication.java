package com.archive.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching 
public class ArchiveBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArchiveBackendApplication.class, args);
	}

}
