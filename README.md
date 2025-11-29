# Kafka Event Driven Microservices

This repository demonstrates an event-driven microservices architecture using Apache Kafka. It contains multiple decoupled services communicating through Kafka topics, modeling real-world scenarios like order processing and payments.

## Overview

Each microservice is responsible for a single business capability and interacts with others exclusively via asynchronous events published to or consumed from Kafka. This design enables loose coupling, scalability, and fault-tolerance for distributed systems.

## Project Structure

- **commons/** — Shared utilities and libraries used by other services.
- **orders-service/** — Handles creating and managing orders, emitting order events.
- **products-service/** — Manages product data and stock, updates product info in response to order/payment events.
- **payments-service/** — Processes payments, listens for order/payment events, and coordinates with the credit card processor.
- **credit-card-processor-service/** — Handles credit card transactions as a specialized payment microservice.

## Technologies Used

- **Apache Kafka** — Event streaming and communication backbone.
- **Spring Boot (Java)** — Framework for developing standalone, production-ready applications.
- **Docker/Docker Compose** — Local orchestration of Kafka and service infrastructure.
- **Maven** — Dependency management and project build system.

## Getting Started

### Prerequisites

- Java JDK 8+ 
- Docker & Docker Compose
- Maven

### Setup

1. **Clone the repository**
    ```sh
    git clone https://github.com/Venkatesh081a/Kafka_Event_Driven_MicroServices.git
    cd Kafka_Event_Driven_MicroServices
    ```
2. **Start local infrastructure (Kafka, Zookeeper, etc.)**
    ```sh
    docker-compose up -d
    ```
3. **Build all services**
    ```sh
    mvn clean install
    ```
4. **Run individual services**
    ```sh
    cd orders-service
    mvn spring-boot:run
    ```
    (Repeat the last two steps for each service you'd like to run.)

## How It Works

- **Order Processing**: The `orders-service` creates an order and emits an event (`OrderCreated`) to a Kafka topic.
- **Payment Handling**: The `payments-service` listens for order events, processes payments (via the credit card service if needed), then emits outcome events.
- **Inventory Updates**: The `products-service` updates stock levels by subscribing to relevant order and payment events.
- **Credit Card Processing**: The `credit-card-processor-service` listens for payment requests and publishes results to Kafka.

## Notable Files

- `docker-compose.yml` — Defines Kafka infrastructure for local development.
- `pom.xml` — Project dependencies.
- Each `*-service/` directory contains an independent Spring Boot application.

## Contributing

Feel free to submit issues or pull requests to improve this reference project.

## License

_No license defined yet. Please create an issue if you'd like to contribute under a specific license._

---

**Note:** This project is for demonstration and educational purposes.