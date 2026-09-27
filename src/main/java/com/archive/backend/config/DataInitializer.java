package com.archive.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.archive.backend.repository.RoleRepository;

@Configuration 
public class DataInitializer {
    
    @Bean 
    public CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {

        };
    }

}
