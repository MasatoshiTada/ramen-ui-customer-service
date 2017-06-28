package com.example.service.impl;

import com.example.service.OrderService;
import com.example.service.dto.OrderSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Source source;

    public OrderServiceImpl(Source source) {
        this.source = source;
    }

    @Override
    public void order(String shopId, OrderSummary orderSummary) {
        logger.info("キューに" + orderSummary.orderDetails.size() + "件の注文を送信します");
        orderSummary.shopId = shopId;
        source.output().send(MessageBuilder.withPayload(orderSummary).build());
    }
}
