package com.example.orderservice.service;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.model.CustomerType;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderStatus;
import com.example.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    public OrderService(OrderRepository orderRepository, RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
    }

    public OrderResponse createOrder(OrderRequest request) {

        BigDecimal total = BigDecimal.valueOf((long) 100 * request.getItems().size());


        Order order = new Order();
        // Convert String → Enum safely
        try {
            order.setCustomerType(CustomerType.valueOf(request.getCustomerType().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid customer type: " + request.getCustomerType());
        }

        order.setTotal(total);
        order.setStatus(OrderStatus.PENDING);

        orderRepository.save(order);

        return mapToResponse(order, "Order created successfully");
    }

    public OrderResponse getOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        return mapToResponse(order, "Order retrieved successfully");
    }

    public OrderResponse updateStatus(Long id, String newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        try {
            order.setStatus(OrderStatus.valueOf(newStatus.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + newStatus);
        }

        orderRepository.save(order);
        return mapToResponse(order, "Order status updated");
    }

    // 🔹 Helper method to avoid repeating mapping logic
    private OrderResponse mapToResponse(Order order, String message) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setStatus(order.getStatus().name());
        response.setMessage(message);
        response.setTotal(order.getTotal().doubleValue());
        return response;
    }
}
