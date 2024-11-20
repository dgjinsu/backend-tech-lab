package com.example.orderquery.global.mongo;

import com.example.orderquery.domain.orderquery.repository.OrderQueryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class MongoDBInitializer {

    private final OrderQueryRepository orderQueryRepository;

    public MongoDBInitializer(OrderQueryRepository orderQueryRepository) {
        this.orderQueryRepository = orderQueryRepository;
    }

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            // 기존 데이터 삭제
            orderQueryRepository.deleteAll();
        };
    }
}