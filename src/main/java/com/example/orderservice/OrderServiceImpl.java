package com.example.orderservice;

import com.example.orderservice.dto.OrderDetailsResponse;
import com.example.orderservice.dto.OrderItemRequest;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository or;
    private final RestTemplate rt;

    @Value("${product.service.base-url}")
    private String productServiceBaseUrl;

    private static final double FREE_SHIPPING_THRESHOLD = 1000.0;
    private static final double FREE_SHIPPING_DISCOUNT = 50.0;
    private static final String DEFAULT_ORDER_STATUS = "PENDING";

    private static final Map<String, Double> DISCOUNT_MAP = new HashMap<>();

    static {
        DISCOUNT_MAP.put("VIP", 0.15);
        DISCOUNT_MAP.put("PREMIUM", 0.10);
        DISCOUNT_MAP.put("REGULAR", 0.05);
        DISCOUNT_MAP.put("NEW", 0.02);
    }

    private static final Map<String, String> STATUS_MESSAGE_MAP = new HashMap<>();

    static {
        STATUS_MESSAGE_MAP.put("PENDING", "Your order is being processed");
        STATUS_MESSAGE_MAP.put("CONFIRMED", "Your order is confirmed");
        STATUS_MESSAGE_MAP.put("SHIPPED", "Your order is on the way");
        STATUS_MESSAGE_MAP.put("DELIVERED", "Your order has been delivered");
        STATUS_MESSAGE_MAP.put("CANCELLED", "Your order was cancelled");
    }

    public OrderServiceImpl(OrderRepository or, RestTemplate rt) {
        this.or = or;
        this.rt = rt;
    }

    @Override
    public Map<Long, Integer> getPopularProducts() {
        List<Order> allOrders = or.findAll();
        Map<Long, Integer> productCounts = new HashMap<>();

        for (Order order : allOrders) {
            for (String item : order.getItems()) {
                Long productId = Long.parseLong(item.split(":")[0]);
                productCounts.put(
                        productId,
                        productCounts.getOrDefault(productId, 0) + 1
                );
            }
        }

        return productCounts;
    }

    @Override
    public void updateOrderStatus(Long orderId, String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Missing or empty status");
        }

        Order order = or.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found"));

        order.setStatus(status);
        or.save(order);
    }

    @Override
    public OrderResponse makeOrder(OrderRequest request) {

        String customerType = request.getCustomerType();
        double discount = DISCOUNT_MAP.getOrDefault(customerType, 0.0);

        Set<Long> productIds = new HashSet<>();
        for (OrderItemRequest item : request.getItems()) {
            productIds.add(item.getProductId());
        }

        Map<Long, Map<String, Object>> productCache = new HashMap<>();

        for (Long productId : productIds) {
            String url = productServiceBaseUrl + "/" + productId;
            Map<String, Object> product = rt.getForObject(url, Map.class);

            if (product == null) {
                throw new IllegalArgumentException("Product not found: " + productId);
            }

            productCache.put(productId, product);
        }

        double total = 0;

        for (OrderItemRequest item : request.getItems()) {

            Map<String, Object> product = productCache.get(item.getProductId());

            double price = Double.parseDouble(product.get("price").toString());
            int stock = Integer.parseInt(product.get("stock").toString());

            if (stock <= 0) {
                throw new IllegalArgumentException(
                        "Product out of stock: " + item.getProductId());
            }

            if (item.getQuantity() > stock) {
                throw new IllegalArgumentException(
                        "Not enough stock for product: " + item.getProductId());
            }

            total += price * item.getQuantity();
        }

        double finalTotal = total - (total * discount);

        if (finalTotal > FREE_SHIPPING_THRESHOLD) {
            finalTotal -= FREE_SHIPPING_DISCOUNT;
        }

        Order order = new Order();
        order.setCustomerType(customerType);
        order.setTotal(finalTotal);
        order.setStatus(DEFAULT_ORDER_STATUS);

        or.save(order);

        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getTotal()
        );
    }

    @Override
    public OrderDetailsResponse getOrder(Long orderId) {

        Order order = or.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found"));

        String status = order.getStatus();
        String message = STATUS_MESSAGE_MAP.getOrDefault(
                status, "Unknown status");

        return new OrderDetailsResponse(
                order.getId(),
                status,
                message,
                order.getTotal()
        );
    }

}
