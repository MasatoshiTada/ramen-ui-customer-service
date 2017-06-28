package com.example.service;

import com.example.service.dto.OrderSummary;

public interface OrderService {

    void order(String shopId, OrderSummary orderSummary);
}
