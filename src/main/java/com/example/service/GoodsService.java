package com.example.service;

import com.example.service.dto.Goods;

import java.util.Map;

public interface GoodsService {

    Map<Integer, Goods> findByIds(Integer[] ids);
}
