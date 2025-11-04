package com.example.notificationsse.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String userId;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    public User(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }
}
