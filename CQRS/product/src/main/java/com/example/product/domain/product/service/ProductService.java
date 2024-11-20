package com.example.product.domain.product.service;

import com.example.product.domain.product.dto.ProductResponse;
import com.example.product.domain.product.dto.ProductSaveRequest;
import com.example.product.domain.product.dto.message.ProductReduceStockRequest;
import com.example.product.domain.product.entity.Product;
import com.example.product.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public void saveProduct(ProductSaveRequest request) {
        Product product = Product.builder()
            .name(request.getName())
            .description(request.getDescription())
            .stock(request.getStock())
            .price(request.getPrice())
            .build();
        productRepository.save(product);
    }

    public ProductResponse getProductById(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));

        return ProductResponse.builder()
            .productId(product.getId())
            .description(product.getDescription())
            .name(product.getName())
            .stock(product.getStock())
            .price(product.getPrice())
            .build();
    }

    public void reduceProductStock(ProductReduceStockRequest request) {
        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new RuntimeException("Product not found"));

        product.reduceStock(request.getQuantity());
    }
}
