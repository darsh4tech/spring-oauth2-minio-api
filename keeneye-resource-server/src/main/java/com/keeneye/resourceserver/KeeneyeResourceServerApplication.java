package com.keeneye.resourceserver;

import java.util.concurrent.TimeUnit;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.github.benmanes.caffeine.cache.Caffeine;

@SpringBootApplication
public class KeeneyeResourceServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(KeeneyeResourceServerApplication.class, args);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
    
    @Bean
    public Caffeine caffeineConfig() {
        return Caffeine.newBuilder().expireAfterWrite(1200, TimeUnit.SECONDS);
    }
    
}
