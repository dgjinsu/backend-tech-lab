package com.example.jaegertest;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Test {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    public Test(String name) {
        this.name = name;
    }
}
