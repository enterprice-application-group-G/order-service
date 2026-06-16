package com.ecommerce.order;

import com.ecommerce.order.controller.OrderController;
import com.ecommerce.order.entity.OrderEntity;
import com.ecommerce.order.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private OrderEntity orderRequest;
    private OrderEntity savedOrder;

    @BeforeEach
    void setUp() {
        orderRequest = new OrderEntity();
        orderRequest.setCustomerId(1L);
        orderRequest.setProductId(1L);
        orderRequest.setQuantity(2);

        savedOrder = new OrderEntity();
        savedOrder.setOrderId(100L);
        savedOrder.setCustomerId(1L);
        savedOrder.setProductId(1L);
        savedOrder.setProductName("Laptop");
        savedOrder.setQuantity(2);
        savedOrder.setTotalPrice(new BigDecimal("2000.00"));
    }

    @Test
    void createOrder_ShouldReturnCreated() throws Exception {
        when(orderService.createOrder(any(OrderEntity.class))).thenReturn(savedOrder);

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(100))
                .andExpect(jsonPath("$.productName").value("Laptop"));
    }
}
