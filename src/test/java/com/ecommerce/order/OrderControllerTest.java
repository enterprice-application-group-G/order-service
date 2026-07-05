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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.ecommerce.order.exception.OrderNotFoundException;

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

    @Test
    void getAllOrders_ShouldReturnOrders() throws Exception {
        when(orderService.getAllOrders()).thenReturn(List.of(savedOrder));

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(100))
                .andExpect(jsonPath("$[0].productName").value("Laptop"));
    }

    @Test
    void getOrderById_ShouldReturnOrder_WhenOrderExists() throws Exception {
        when(orderService.getOrderById(100L)).thenReturn(savedOrder);

        mockMvc.perform(get("/orders/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(100))
                .andExpect(jsonPath("$.productName").value("Laptop"));
    }

    @Test
    void getOrderById_ShouldReturnNotFound_WhenOrderDoesNotExist() throws Exception {
        doThrow(new OrderNotFoundException(999L)).when(orderService).getOrderById(999L);

        mockMvc.perform(get("/orders/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Order not found with ID: 999"));
    }
}
