package com.example.orderservice.dto;


public class OrderDetailsResponse {

    private Long orderId;
    private String status;
    private String message;
    private double total;

    public OrderDetailsResponse(Long orderId, String status, String message, double total) {
        this.orderId = orderId;
        this.status = status;
        this.message = message;
        this.total = total;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public double getTotal() {
        return total;
    }
}

