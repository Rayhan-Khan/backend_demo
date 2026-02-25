package com.healthfix;

import com.healthfix.utils.DotenvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HealthfixApplication {
    public static void main(String[] args) {
        DotenvLoader.loadDotenv(); // Load .env file
        SpringApplication.run(HealthfixApplication.class, args);
    }
}
