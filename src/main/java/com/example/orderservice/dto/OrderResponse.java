package com.example.orderservice.dto;

public class OrderResponse {

    private Long orderId;
    private String status;
    private double total;

    public OrderResponse(Long orderId, String status, double total) {
        this.orderId = orderId;
        this.status = status;
        this.total = total;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getStatus() {
        return status;
    }

    public double getTotal() {
        return total;
    }
}

