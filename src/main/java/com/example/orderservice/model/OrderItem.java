package com.example.orderservice.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class OrderItem {
    private String productId;
    private int quantity;

    // getters and setters
    public String getProductId() {
        return productId;
    }
    public void setProductId(String productId) {
        this.productId = productId;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
