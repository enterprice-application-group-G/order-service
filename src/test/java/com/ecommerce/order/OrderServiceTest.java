package com.ecommerce.order;

import com.ecommerce.order.config.AppConfig;
import com.ecommerce.order.dto.OrderMessage;
import com.ecommerce.order.dto.ProductResponse;
import com.ecommerce.order.entity.OrderEntity;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private OrderService orderService;

    private OrderEntity orderRequest;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(orderService, "productServiceUrl", "http://localhost:8081");

        orderRequest = new OrderEntity();
        orderRequest.setCustomerId(1L);
        orderRequest.setProductId(1L);
        orderRequest.setQuantity(2);

        productResponse = new ProductResponse();
        productResponse.setProductId(1L);
        productResponse.setName("Laptop");
        productResponse.setUnitPrice(new BigDecimal("1000.00"));
    }

    @Test
    void createOrder_ShouldCalculateTotalAndPublish() {
        when(restTemplate.getForObject(anyString(), eq(ProductResponse.class))).thenReturn(productResponse);
        
        OrderEntity savedOrder = new OrderEntity();
        savedOrder.setOrderId(100L);
        savedOrder.setCustomerId(1L);
        savedOrder.setProductId(1L);
        savedOrder.setProductName("Laptop");
        savedOrder.setQuantity(2);
        savedOrder.setTotalPrice(new BigDecimal("2000.00"));
        
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(savedOrder);

        OrderEntity result = orderService.createOrder(orderRequest);

        assertNotNull(result);
        assertEquals(new BigDecimal("2000.00"), result.getTotalPrice());
        assertEquals("Laptop", result.getProductName());

        verify(orderRepository, times(1)).save(any(OrderEntity.class));
        verify(rabbitTemplate, times(1)).convertAndSend(eq(AppConfig.QUEUE_NAME), any(OrderMessage.class));
    }

    @Test
    void createOrder_ShouldThrowException_WhenProductNotFound() {
        when(restTemplate.getForObject(anyString(), eq(ProductResponse.class))).thenReturn(null);

        assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(orderRequest);
        });

        verify(orderRepository, never()).save(any(OrderEntity.class));
        verify(rabbitTemplate, never()).convertAndSend(anyString(), any(OrderMessage.class));
    }
}
