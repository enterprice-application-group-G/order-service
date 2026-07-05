# Order Service

The Order Service is a microservice responsible for orchestrating the e-commerce checkout process. It interacts with the **Product Service** to validate product details and calculate pricing, stores the order in its own PostgreSQL database, and publishes an event to **RabbitMQ** for asynchronous processing.

## Tech Stack
* Java 17
* Spring Boot 3.5.0
* Spring Data JPA
* PostgreSQL
* RabbitMQ (AMQP)
* Spring Boot RestTemplate
* GitHub Actions (CI/CD)

## Features
* **Create Order**: `POST /orders`
  - Validates the product exists by calling the Product Service.
  - Calculates the total order price.
  - Saves the order record.
  - Dispatches an `OrderMessage` to the RabbitMQ `order_queue`.
* **List Orders**: `GET /orders`
  - Returns all stored orders.
* **Get Order By ID**: `GET /orders/{orderId}`
  - Returns one order by ID.
  - Returns `404 Not Found` when the order does not exist.

## Running Locally

1. **Start dependencies**:
   This service requires PostgreSQL and RabbitMQ. A `compose.yaml` is provided to spin them both up.
   ```bash
   docker compose up -d
   ```

2. **Ensure Product Service is running**:
   The Order Service expects the Product Service to be running on `http://localhost:8081`.

3. **Run the application**:
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Access API Documentation**:
   Swagger UI is available at:
   [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)

## CI/CD Pipeline
This repository contains a GitHub Actions workflow (`.github/workflows/ci.yml`) that automatically runs unit tests and executes SonarCloud static analysis whenever a Pull Request is opened against the `main` branch.
