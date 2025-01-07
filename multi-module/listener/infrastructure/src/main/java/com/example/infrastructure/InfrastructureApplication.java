package com.example.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
	"com.example.application",  // application 패키지 스캔
	"com.example.infrastructure", // 현재 infrastructure 패키지도 스캔
	"com.example.persistence"      // persistence 패키지 스캔
})
public class InfrastructureApplication {

	public static void main(String[] args) {
		SpringApplication.run(InfrastructureApplication.class, args);
	}

}
