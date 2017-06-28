package com.example.service.dto;

import java.io.Serializable;
import java.util.List;

public class OrderSummary implements Serializable {

    public List<OrderDetail> orderDetails;

    public String shopId;

}
