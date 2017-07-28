package com.example.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class OrderSummary implements Serializable {

    private Integer summaryId;

    /**
     * 注文日時
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Tokyo")
    private LocalDateTime orderTimestamp;

    /**
     * お客さんに提供済みならばtrue
     */
    private Boolean provided;

    private List<OrderDetail> orderDetails;

    /**
     * 店舗ID
     */
    private String shopId;

    public Integer getSummaryId() {
        return summaryId;
    }

    public void setSummaryId(Integer summaryId) {
        this.summaryId = summaryId;
    }

    public LocalDateTime getOrderTimestamp() {
        return orderTimestamp;
    }

    public void setOrderTimestamp(LocalDateTime orderTimestamp) {
        this.orderTimestamp = orderTimestamp;
    }

    public Boolean getProvided() {
        return provided;
    }

    public void setProvided(Boolean provided) {
        this.provided = provided;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    @Override
    public String toString() {
        return "OrderSummary{" +
                "summaryId=" + summaryId +
                ", orderTimestamp=" + orderTimestamp +
                ", provided=" + provided +
                ", orderDetails=" + orderDetails +
                ", shopId='" + shopId + '\'' +
                '}';
    }
}
