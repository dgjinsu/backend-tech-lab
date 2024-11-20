package com.example.product.domain.product.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ProductSaveRequest {
    private String name;
    private String description;
    private Integer price;
    private Integer stock;
}
