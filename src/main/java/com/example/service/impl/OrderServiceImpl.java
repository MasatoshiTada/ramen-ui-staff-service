package com.example.service.impl;

import com.example.persistence.redis.dto.OrderSummaryListDto;
import com.example.persistence.redis.repository.OrderSummaryListDtoRepository;
import com.example.service.GoodsService;
import com.example.service.OrderService;
import com.example.service.dto.Goods;
import com.example.service.dto.OrderDetail;
import com.example.service.dto.OrderSummary;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RestTemplate restTemplate;
    private final GoodsService goodsService;
    private final String orderServiceUrl;
    private final OrderSummaryListDtoRepository orderSummaryListDtoRepository;
    private final Source source;

    public OrderServiceImpl(@LoadBalanced RestTemplate restTemplate,
                            GoodsService goodsService,
                            @Value("${orders.url}") String orderServiceUrl,
                            OrderSummaryListDtoRepository orderSummaryListDtoRepository,
                            Source source) {
        this.restTemplate = restTemplate;
        this.goodsService = goodsService;
        this.orderServiceUrl = orderServiceUrl;
        this.orderSummaryListDtoRepository = orderSummaryListDtoRepository;
        this.source = source;
    }

    /**
     * API Gateway経由でorder-serviceから未提供の注文一覧を取得する。
     */
    @HystrixCommand(fallbackMethod = "createDefaultOrders")
    @Override
    public List<OrderSummary> findAllNotProvided(String shopId) {
        logger.info("{}の注文一覧を取得します", shopId);
        OrderSummary[] orders = restTemplate.getForObject(orderServiceUrl + "/shop/{shopId}", OrderSummary[].class, shopId);
        List<OrderSummary> orderList = Arrays.asList(orders);

        Integer[] goodsIds = orderList.stream()
                .flatMap(orderSummary -> orderSummary.getOrderDetails().stream())
                .map(orderDetail -> orderDetail.getGoodsId())
                .toArray(Integer[]::new);
        Map<Integer, Goods> goodsMap = goodsService.findByIds(goodsIds);

        for (OrderSummary orderSummary : orderList) {
            for (OrderDetail orderDetail : orderSummary.getOrderDetails()) {
                orderDetail.setGoods(goodsMap.get(orderDetail.getGoodsId()));
            }
        }
        // Redisにキャッシュ
        OrderSummaryListDto orderSummaryListDto = new OrderSummaryListDto(shopId, orderList);
        logger.info("取得した注文一覧をRedisに保存しました");
        orderSummaryListDtoRepository.save(orderSummaryListDto);
        return orderList;
    }

    /**
     * 注文一覧取得に対するフォールバックメソッド。
     * キャッシュしていた注文一覧を返す。
     */
    public List<OrderSummary> createDefaultOrders(String shopId, Throwable throwable) {
        logger.error("staff-api-gatewayへの接続に失敗しました。フォールバックします。", throwable);
        logger.info("Redisからキャッシュの注文一覧を取得します");
        OrderSummaryListDto orderSummaryListDto = orderSummaryListDtoRepository.findOne(shopId);
        if (orderSummaryListDto != null) {
            logger.info("Redisに注文一覧がありました");
            return orderSummaryListDto.getOrderSummaryList();
        } else {
            logger.error("Redisに注文一覧がありませんでした");
            return Collections.emptyList();
        }
    }

    /**
     * 指定した注文を「提供済み」に更新する。
     * @param summaryId
     */
    @Override
    public void updateProvided(String shopId, Integer summaryId) {
        logger.info("注文済みに更新します : shopId = {}, summaryId = {}", shopId, summaryId);

        // キューに更新情報を送信
        SummaryIdHolder summaryIdHolder = new SummaryIdHolder(summaryId);
        source.output().send(MessageBuilder.withPayload(summaryIdHolder).build());

        // Redisからキャッシュ取得
        logger.info("Redisからキャッシュの注文一覧を取得します");
        OrderSummaryListDto orderSummaryListDto = orderSummaryListDtoRepository.findOne(shopId);
        // 更新した注文だけキャッシュから削除
        List<OrderSummary> orderSummaryList = orderSummaryListDto.getOrderSummaryList();
        List<OrderSummary> filteredOrderSummaryList = orderSummaryList.stream()
                .filter(orderSummary -> orderSummary.getSummaryId().equals(summaryId) == false)
                .collect(Collectors.toList());
        // キャッシュに再保存
        logger.info("Redisに注文一覧を再保存します");
        orderSummaryListDtoRepository.save(new OrderSummaryListDto(shopId, filteredOrderSummaryList));
    }

    private static class SummaryIdHolder {
        private Integer summaryId;

        public SummaryIdHolder(Integer summaryId) {
            this.summaryId = summaryId;
        }

        public Integer getSummaryId() {
            return summaryId;
        }

        public void setSummaryId(Integer summaryId) {
            this.summaryId = summaryId;
        }
    }
}
