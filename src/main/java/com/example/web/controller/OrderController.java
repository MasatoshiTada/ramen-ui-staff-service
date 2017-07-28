package com.example.web.controller;

import com.example.service.OrderService;
import com.example.service.dto.OrderSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String index(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String shopId = authentication.getName();
        logger.info("shopId = {}の注文一覧を取得します...", shopId);
        List<OrderSummary> orderSummaryList = orderService.findAllNotProvided(shopId);
        logger.info("shopId = {}の注文一覧＝{}", shopId, orderSummaryList);
        model.addAttribute("orderSummaryList", orderSummaryList);
        return "index";
    }

    @PostMapping("/{summaryId}")
    public String updateProvided(@PathVariable("summaryId") Integer summaryId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String shopId = authentication.getName();
        orderService.updateProvided(shopId, summaryId);
        return "redirect:/order";
    }

}
