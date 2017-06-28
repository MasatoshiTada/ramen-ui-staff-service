package com.example.service.impl;

import com.example.service.GoodsService;
import com.example.service.dto.Goods;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GoodsServiceImpl implements GoodsService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RestTemplate restTemplate;
    private final String goodsServiceUrl;

    public GoodsServiceImpl(@LoadBalanced RestTemplate restTemplate,
                            @Value("${goods.url}") String goodsServiceUrl) {
        this.restTemplate = restTemplate;
        this.goodsServiceUrl = goodsServiceUrl;
    }

    @HystrixCommand(fallbackMethod = "fallbackFindByIds")
    @Override
    public Map<Integer, Goods> findByIds(Integer[] ids) {
        String queryString = Arrays.stream(ids)
                .distinct()
                .map(id -> id.toString())
                .collect(Collectors.joining("&id=", "id=", ""));
        String url = goodsServiceUrl + "/findByIds?" + queryString;
        ResponseEntity<List<Goods>> responseEntity =
                restTemplate.exchange(url, HttpMethod.GET,
                        null, new ParameterizedTypeReference<List<Goods>>() {
                        });
        Map<Integer, Goods> goodsMap = responseEntity.getBody()
                .stream()
                .collect(Collectors.toMap(goods -> goods.getId(), goods -> goods));
        return goodsMap;
    }

    public Map<Integer, Goods> fallbackFindByIds(Integer[] ids, Throwable throwable) {
        logger.error("商品データの取得に失敗しました。フォールバックします。", throwable);
        // FIXME 商品データ取得失敗時のフォールバック処理どうすべき？
        return Collections.emptyMap();
    }
}