package com.example.orderservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {
    @org.jetbrains.annotations.NotNull
    private String customerType;

    @NotNull
    private List<OrderItemDto> items;
}
