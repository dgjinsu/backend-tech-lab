package com.example.order.global.feign;

import com.example.order.domain.order.dto.feign.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product", url = "http://localhost:8082")
public interface ProductClient {
    @GetMapping("/api/products/{productId}")
    ProductResponse getProducts(@PathVariable("productId") Long productId);
}
