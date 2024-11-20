package com.example.product.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ProductResponse {
    private Long productId;
    private String name;
    private String description;
    private Integer price;
    private Integer stock;
}
