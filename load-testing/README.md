# Order Service JMeter Load Test

This folder contains a JMeter test plan for the order service.

## What it tests
- GET /orders
- GET /orders/{orderId}

## Before running
1. Start PostgreSQL and RabbitMQ:
   ```powershell
   cd order-service
   docker compose up -d
   ```

2. Start the order service locally:
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

3. Make sure at least one order exists in the database before testing the order-by-id request.

## Open and run in JMeter
1. Open Apache JMeter.
2. Open `order-service-read-load-test.jmx`.
3. Click Run.
4. Review the Summary Report.

## Suggested first values
- Threads: 25
- Ramp-up: 10 seconds
- Loop count: 20
