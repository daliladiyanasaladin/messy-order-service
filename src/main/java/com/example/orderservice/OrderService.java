package com.example.orderservice;

import com.example.orderservice.dto.OrderDetailsResponse;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;

import java.util.Map;

public interface OrderService {

    Map<Long, Integer> getPopularProducts();

    void updateOrderStatus(Long orderId, String status);

    OrderResponse makeOrder(OrderRequest request);

    OrderDetailsResponse getOrder(Long orderId);

}
