package com.example.persistence.redis.dto;

import com.example.service.dto.OrderSummary;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@RedisHash(value = "order_summary_list")
public class OrderSummaryListDto {

    @Id
    private String shopId;

    private List<OrderSummary> orderSummaryList;

    public OrderSummaryListDto() {
    }

    public OrderSummaryListDto(String shopId, List<OrderSummary> orderSummaryList) {
        this.setShopId(shopId);
        this.setOrderSummaryList(orderSummaryList);
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public List<OrderSummary> getOrderSummaryList() {
        return orderSummaryList;
    }

    public void setOrderSummaryList(List<OrderSummary> orderSummaryList) {
        this.orderSummaryList = orderSummaryList;
    }
}
