package com.example.nexusnotificationservice;

import com.example.nexussse.global.config.NexusSseConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(NexusSseConfig.class)
public class NexusNotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NexusNotificationServiceApplication.class, args);
    }

}