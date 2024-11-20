package com.example.product.domain.product.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Product {
    @Id
    @GeneratedValue
    @Column(name = "product_id")
    private Long id;

    private String name;
    private String description;
    private Integer price;
    private Integer stock;

    @Builder
    public Product(String name, String description, Integer price, Integer stock) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
    }

    public void reduceStock(Integer quantity) {
        if (this.stock < quantity) {
            throw new IllegalArgumentException("Not enough stock");
        }
        this.price -= quantity;
    }
}
