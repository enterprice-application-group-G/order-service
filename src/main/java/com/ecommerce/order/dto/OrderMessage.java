package com.ecommerce.order.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class OrderMessage implements Serializable {
    private Long orderId;
    private Long customerId;
    private String productName;
    private Integer quantity;
    private LocalDateTime timestamp;

    public OrderMessage() {}

    public OrderMessage(Long orderId, Long customerId, String productName, Integer quantity, LocalDateTime timestamp) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.productName = productName;
        this.quantity = quantity;
        this.timestamp = timestamp;
    }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
