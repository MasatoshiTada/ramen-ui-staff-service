package com.example.service;

import com.example.service.dto.OrderSummary;

import java.util.List;

public interface OrderService {

    List<OrderSummary> findAllNotProvided(String shopId);

    void updateProvided(String shopId, Integer summaryId);
}
