package com.ecommerce.order;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

@SpringBootTest
class OrderServiceApplicationTests {

	@MockBean
	private ConnectionFactory connectionFactory;

	@Test
	void contextLoads() {
	}

}
