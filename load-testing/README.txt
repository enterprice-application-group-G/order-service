Order Service Load Testing

This folder contains a JMeter test plan for the Order Service read endpoints.

File:
- order-service-read-load-test.jmx

Endpoints tested:
- GET /orders
- GET /orders/{orderId}

Default variables inside the JMeter plan:
- protocol = http
- host = localhost
- port = 8082
- threads = 25
- ramp_up = 10
- loop_count = 20
- order_id = 1

Before running:
1. Start PostgreSQL and RabbitMQ:
   docker compose up -d

2. Start the Order Service:
   .\mvnw.cmd spring-boot:run

3. Make sure at least one order exists in the database.
   The GET /orders/{orderId} request expects order_id to be an existing order ID.

How to run in JMeter GUI:
1. Open Apache JMeter.
2. Open order-service-read-load-test.jmx.
3. Update the variables if needed.
4. Click Start.
5. Check the Summary Report.

How to run from command line:
jmeter -n -t load-testing/order-service-read-load-test.jmx -l load-testing/results.jtl

Example with custom values:
jmeter -n -t load-testing/order-service-read-load-test.jmx -l load-testing/results.jtl -Jthreads=50 -Jramp_up=20 -Jloop_count=30 -Jorder_id=1
