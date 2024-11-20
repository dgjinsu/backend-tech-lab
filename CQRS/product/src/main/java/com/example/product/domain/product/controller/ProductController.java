package com.example.product.domain.product.controller;

import com.example.product.domain.product.dto.ProductResponse;
import com.example.product.domain.product.dto.ProductSaveRequest;
import com.example.product.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping("")
    public ResponseEntity<String> saveProduct(@RequestBody ProductSaveRequest request) {
        productService.saveProduct(request);
        return ResponseEntity.ok("저장 완료");
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }
}
