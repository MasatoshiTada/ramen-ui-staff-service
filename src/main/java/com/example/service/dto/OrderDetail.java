package com.example.service.dto;

import java.io.Serializable;

public class OrderDetail implements Serializable {

    private Integer detailId;

    private Integer goodsId;

    private Integer amount;

    private Goods goods;

    public Integer getDetailId() {
        return detailId;
    }

    public void setDetailId(Integer detailId) {
        this.detailId = detailId;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    @Override
    public String toString() {
        return "OrderDetail{" +
                "detailId=" + detailId +
                ", goodsId=" + goodsId +
                ", amount=" + amount +
                ", goods=" + goods +
                '}';
    }
}
