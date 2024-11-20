package com.example.orderquery.domain.orderquery.repository;

import com.example.orderquery.domain.orderquery.entity.OrderQueryModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderQueryRepository extends MongoRepository<OrderQueryModel, Long> {

}
