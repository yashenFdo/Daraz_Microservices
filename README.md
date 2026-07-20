# Daraz E-Commerce Microservices Architecture

This repository contains the backend microservices architecture for a scalable, decoupled e-commerce platform inspired by Daraz.lk. The project has been successfully migrated from a monolithic Spring Boot application to a Domain-Driven Design (DDD) based microservices ecosystem.

## 🏗️ Architecture Overview

The system is composed of several independent microservices communicating seamlessly through Spring Cloud Netflix Eureka for service discovery, a central Spring Cloud Config Server for configuration management, and a Spring Cloud API Gateway serving as the single entry point.

Asynchronous communication (e.g., user registration, review additions) is handled via **Apache Kafka**, while synchronous inter-service communication (e.g., Cart fetching product details) is handled via **OpenFeign**.

## 🚀 Tech Stack

*   **Language:** Java 21
*   **Framework:** Spring Boot 3.4.1, Spring Cloud 2024.0.0
*   **Databases:** 
    *   PostgreSQL (User Service, Wishlist Service)
    *   MongoDB (Catalog Service)
    *   Redis (Cart Service)
*   **Message Broker:** Apache Kafka & Zookeeper
*   **Security:** Spring Security & JWT (Stateless Authentication)
*   **API Gateway:** Spring Cloud Gateway (Reactive)
*   **Service Discovery:** Netflix Eureka
*   **Containerization & Orchestration:** Docker & Docker Compose
*   **Mapping:** MapStruct

---

## 📦 Microservices Breakdown

### 1. Infrastructure Services
*   **Config Server (`:8888`)**: Centralized configuration management fetching environment-specific YAML files.
*   **Discovery Server (`:8761`)**: Eureka Server where all microservices register themselves dynamically.
*   **API Gateway (`:8080`)**: The single entry point for all client requests. It validates JWT tokens, strips the `Authorization` header, and injects an `X-User-Id` header to downstream services.

### 2. Core Business Services
*   **User Service (`:8081`)**: 
    *   **DB:** PostgreSQL (`daraz_users`)
    *   **Responsibilities:** Handles customer registration, login (issues JWTs), and profile management. Publishes Kafka events upon successful registration.
*   **Catalog Service (`:8082`)**: 
    *   **DB:** MongoDB
    *   **Responsibilities:** Manages products, nested categories, and reviews. Handles search and indexing for fast product discovery. Publishes Kafka events for new product reviews.
*   **Cart Service (`:8083`)**: 
    *   **DB:** Redis
    *   **Responsibilities:** Manages shopping carts using fast, in-memory caching. Uses OpenFeign to communicate with the Catalog Service to validate product availability and prices before adding them to the cart.
*   **Wishlist Service (`:8084`)**: 
    *   **DB:** PostgreSQL (`daraz_wishlists`)
    *   **Responsibilities:** Manages customer wishlists. Uses OpenFeign to interact with the Catalog Service.

---

## ⚙️ How to Run Locally

### Prerequisites
*   [Docker](https://www.docker.com/products/docker-desktop/) and Docker Compose installed.
*   Port availability: Ensure ports `8080`, `8081`, `8082`, `8083`, `8084`, `8761`, `8888`, `5432` (Postgres), `5433` (Postgres 2), `27017` (Mongo), `6379` (Redis), `9092` (Kafka), and `2181` (Zookeeper) are not currently in use by other local applications.

### Start the Ecosystem

1. Clone the repository:
   ```bash
   git clone https://github.com/yashenFdo/Daraz_Microservices.git
   cd Daraz_Microservices
   ```

2. Spin up the entire infrastructure using Docker Compose:
   ```bash
   docker-compose up -d --build
   ```
   *This command will download the necessary database/broker images, build the Spring Boot `.jar` files within Docker using multi-stage builds (or pre-compiled jars), and start all containers in the correct dependency order.*

3. **Verify Deployment:**
   *   Eureka Dashboard: `http://localhost:8761` (Check that all instances are registered. This might take 1-2 minutes).
   *   API Gateway: `http://localhost:8080`

### Stop the Ecosystem
To gracefully stop the containers and preserve data volumes:
```bash
docker-compose down
```

---

## 🌐 API Routing via Gateway (`localhost:8080`)

All client requests should be routed through the API Gateway on port `8080`. The Gateway routes are configured as follows:

| Service | Gateway Path | Required Auth |
| :--- | :--- | :--- |
| **User Service** (Auth) | `/api/v1/auth/**` | Public (Permit All) |
| **User Service** (Profile)| `/api/v1/customers/**` | Valid JWT Required |
| **Catalog Service** | `/api/v1/products/**`, `/api/v1/categories/**` | Valid JWT Required (can be made public for GET reqs) |
| **Cart Service** | `/api/v1/cart/**` | Valid JWT Required |
| **Wishlist Service** | `/api/v1/wishlists/**` | Valid JWT Required |

### Authentication Flow
1. Client calls `POST http://localhost:8080/api/v1/auth/login` with credentials.
2. User Service validates and returns a JWT.
3. Client includes `Authorization: Bearer <token>` in the header for all subsequent requests to the Gateway.
4. Gateway intercepts the request, validates the JWT signature, extracts the user ID, and forwards the request to the appropriate microservice with an `X-User-Id` header.

## 🤝 Contribution Guidelines
Follow the standard Git workflow:
1. Create a feature branch (`git checkout -b feature/my-feature`).
2. Commit your changes with descriptive messages (`git commit -m 'feat(cart): add bulk delete'`).
3. Push to the branch (`git push origin feature/my-feature`).
4. Open a Pull Request.
