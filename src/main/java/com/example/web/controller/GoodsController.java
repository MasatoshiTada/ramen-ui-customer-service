package com.example.web.controller;

import com.example.service.GoodsService;
import com.example.service.OrderService;
import com.example.service.dto.Goods;
import com.example.service.dto.OrderDetail;
import com.example.service.dto.OrderSummary;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    private final GoodsService goodsService;
    private final OrderService orderService;

    public GoodsController(GoodsService goodsService, OrderService orderService) {
        this.goodsService = goodsService;
        this.orderService = orderService;
    }

    @GetMapping
    public String index(Model model) {
        List<Goods> goodsList = goodsService.findAll();
        model.addAttribute("goodsList", goodsList);
        return "index";
    }

    @PostMapping("/order")
    public String order(@RequestParam Map<String, String> params) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String shopId = authentication.getName();
        OrderSummary orderSummary = convertToOrderSummary(params);
        orderService.order(shopId, orderSummary);
        return "redirect:orderComplete";
    }

    @GetMapping("/orderComplete")
    public String orderComplete() {
        return "orderComplete";
    }

    private OrderSummary convertToOrderSummary(Map<String, String> params) {
        List<OrderDetail> orderDetailList = params.entrySet()
                .stream()
                .filter(entry -> entry.getKey().startsWith("goodsId_"))
                .map(entry -> {
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.goodsId = Integer.valueOf(entry.getKey().split("_")[1]);
                    orderDetail.amount = Integer.valueOf(entry.getValue());
                    return orderDetail;
                })
                .filter(orderDetail -> !orderDetail.amount.equals(0))
                .collect(Collectors.toList());
        OrderSummary orderSummary = new OrderSummary();
        orderSummary.orderDetails = orderDetailList;
        return orderSummary;
    }

}
