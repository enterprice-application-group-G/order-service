package com.ecommerce.order.service;

import com.ecommerce.order.config.AppConfig;
import com.ecommerce.order.dto.OrderMessage;
import com.ecommerce.order.dto.ProductResponse;
import com.ecommerce.order.entity.OrderEntity;
import com.ecommerce.order.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;
    private final RabbitTemplate rabbitTemplate;

    @Value("${product-service.url}")
    private String productServiceUrl;

    public OrderService(OrderRepository orderRepository, RestTemplate restTemplate, RabbitTemplate rabbitTemplate) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
        this.rabbitTemplate = rabbitTemplate;
    }

    public OrderEntity createOrder(OrderEntity orderRequest) {
        // 1. Call Product Service to fetch product details
        String url = productServiceUrl + "/products/" + orderRequest.getProductId();
        ProductResponse product;
        try {
            product = restTemplate.getForObject(url, ProductResponse.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Product not found with ID: " + orderRequest.getProductId());
        } catch (Exception e) {
            throw new RuntimeException("Error communicating with Product Service: " + e.getMessage());
        }

        if (product == null) {
            throw new RuntimeException("Product not found with ID: " + orderRequest.getProductId());
        }

        // 2. Calculate total price
        BigDecimal totalPrice = product.getUnitPrice().multiply(new BigDecimal(orderRequest.getQuantity()));

        // 3. Store order in database
        orderRequest.setProductName(product.getName());
        orderRequest.setTotalPrice(totalPrice);
        orderRequest.setOrderDate(LocalDateTime.now());
        orderRequest.setStatus("CREATED");
        
        OrderEntity savedOrder = orderRepository.save(orderRequest);

        // 4. Publish order event to RabbitMQ
        OrderMessage message = new OrderMessage(
                savedOrder.getOrderId(),
                savedOrder.getCustomerId(),
                savedOrder.getProductName(),
                savedOrder.getQuantity(),
                savedOrder.getOrderDate()
        );
        
        rabbitTemplate.convertAndSend(AppConfig.QUEUE_NAME, message);
        logger.info("Order message published to RabbitMQ for Order ID: {}", savedOrder.getOrderId());

        return savedOrder;
    }
}
