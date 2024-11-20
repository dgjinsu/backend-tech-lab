package com.example.product.domain.product.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ProductReduceStockRequest {
    private Long productId;
    private Integer quantity;
}
