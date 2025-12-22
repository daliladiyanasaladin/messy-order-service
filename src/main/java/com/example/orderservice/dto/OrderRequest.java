package com.example.orderservice.dto;

import java.util.List;

public class OrderRequest {
    private String customerType;
    private List<OrderItemRequest> items;

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
}
