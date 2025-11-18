package com.example.springfqcn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@SpringBootApplication
@ComponentScan(
    basePackages = "com.example.springfqcn",
    nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
public class SpringFqcnApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringFqcnApplication.class, args);
    }

}
