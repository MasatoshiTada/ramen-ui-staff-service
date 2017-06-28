package com.example.persistence.redis.repository;


import com.example.persistence.redis.dto.OrderSummaryListDto;
import org.springframework.data.repository.CrudRepository;

public interface OrderSummaryListDtoRepository extends CrudRepository<OrderSummaryListDto, String> {
}
