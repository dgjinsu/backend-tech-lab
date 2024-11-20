package com.example.order.domain.orderquery.repository;

import com.example.order.domain.orderquery.entity.OrderQueryModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderQueryRepository extends MongoRepository<OrderQueryModel, Long> {

}
