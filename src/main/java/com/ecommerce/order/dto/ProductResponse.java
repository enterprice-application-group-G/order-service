package com.ecommerce.order.dto;

import java.math.BigDecimal;

public class ProductResponse {
    private Long productId;
    private String name;
    private BigDecimal unitPrice;
    
    public ProductResponse() {}

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}
