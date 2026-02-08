package com.example.architecturestudy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ArchitectureStudyApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArchitectureStudyApplication.class, args);
    }

}
