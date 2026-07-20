# Daraz.lk Microservices – Full Implementation Plan

## Overview

A production-grade, Maven multi-module microservices clone of Daraz.lk built on **Java 21 + Spring Boot 3.4.x + Spring Cloud**. All services are containerised via Docker with a `docker-compose.yml` for local development.

---

## Architecture Diagram

```
Client (Browser/Mobile)
        │
        ▼
┌─────────────────────┐
│    API Gateway       │  :8080  (Spring Cloud Gateway + JWT filter)
└──────────┬──────────┘
           │  routes (lb://)
     ┌─────┴──────────────────────────────────────────────┐
     │                                                    │
     ▼                ▼                ▼                  ▼
┌──────────┐   ┌────────────┐  ┌────────────┐  ┌────────────────┐
│  User    │   │  Catalog   │  │   Cart     │  │   Wishlist     │
│ Service  │   │  Service   │  │  Service   │  │   Service      │
│  :8081   │   │   :8082    │  │   :8083    │  │    :8084       │
└────┬─────┘   └─────┬──────┘  └─────┬──────┘  └───────┬────────┘
     │               │               │                  │
     ▼               ▼               ▼                  ▼
  MySQL/PG        MongoDB         Redis              MySQL/PG

                    ▲ ▲ ▲ ▲
              (all register with)
          ┌─────────────────────────┐
          │   Discovery Server      │  :8761  (Eureka)
          └─────────────────────────┘
          ┌─────────────────────────┐
          │   Config Server         │  :8888  (Spring Cloud Config)
          └─────────────────────────┘
          ┌─────────────────────────┐
          │   Apache Kafka          │  :9092  (Async events)
          └─────────────────────────┘
```

---

## Repository Structure (Maven Multi-Module)

```
daraz-microservices/
├── pom.xml                     ← Root aggregator POM (BOM, plugin mgmt)
├── docker-compose.yml          ← Full local dev stack
├── .env                        ← Environment variables
│
├── config-server/              ← Spring Cloud Config Server  :8888
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── configs/           ← per-service YAML files
│   └── pom.xml
│
├── discovery-server/           ← Eureka Server               :8761
│   ├── src/main/...
│   └── pom.xml
│
├── api-gateway/                ← Spring Cloud Gateway        :8080
│   ├── src/main/...            (JWT validation filter, routes)
│   └── pom.xml
│
├── user-service/               ← Customer auth & profile     :8081
│   ├── src/main/java/lk/daraz/userservice/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   ├── dto/
│   │   ├── security/           (JWT provider, filter)
│   │   ├── event/              (Kafka producers)
│   │   └── mapper/             (MapStruct)
│   └── pom.xml
│
├── catalog-service/            ← Products, Categories, Reviews :8082
│   ├── src/main/java/lk/daraz/catalogservice/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── document/           (MongoDB documents)
│   │   ├── dto/
│   │   ├── event/
│   │   └── mapper/
│   └── pom.xml
│
├── cart-service/               ← Shopping cart (Redis)        :8083
│   ├── src/main/java/lk/daraz/cartservice/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── model/
│   │   ├── dto/
│   │   └── client/             (FeignClient → catalog-service)
│   └── pom.xml
│
└── wishlist-service/           ← Wishlist (PostgreSQL)        :8084
    ├── src/main/java/lk/daraz/wishlistservice/
    │   ├── controller/
    │   ├── service/
    │   ├── repository/
    │   ├── entity/
    │   ├── dto/
    │   └── client/
    └── pom.xml
```

---

## Files to be Created (grouped by module)

### Root Level
| File | Purpose |
|------|---------|
| `pom.xml` | Aggregator POM with dependency management |
| `docker-compose.yml` | Full stack: services + infra |
| `.env` | Environment variable defaults |
| `.gitignore` | Standard Java/Maven ignores |

### config-server
| File | Purpose |
|------|---------|
| `pom.xml` | Spring Cloud Config Server deps |
| `src/.../ConfigServerApplication.java` | `@EnableConfigServer` |
| `src/main/resources/application.yml` | Native file backend config |
| `src/main/resources/configs/*.yml` | Per-service configs |
| `Dockerfile` | Container definition |

### discovery-server
| File | Purpose |
|------|---------|
| `pom.xml` | Eureka Server deps |
| `src/.../DiscoveryServerApplication.java` | `@EnableEurekaServer` |
| `src/main/resources/application.yml` | Eureka server config |
| `Dockerfile` | Container definition |

### api-gateway
| File | Purpose |
|------|---------|
| `pom.xml` | Gateway + Security deps |
| `src/.../ApiGatewayApplication.java` | Main class |
| `src/.../filter/JwtAuthenticationFilter.java` | JWT validation |
| `src/.../config/GatewayConfig.java` | Route definitions |
| `src/main/resources/application.yml` | Routes + Eureka client |
| `Dockerfile` | Container definition |

### user-service
| File | Purpose |
|------|---------|
| `pom.xml` | Spring Boot, Security, JPA, Kafka, MapStruct |
| `entity/Customer.java` | JPA entity |
| `dto/RegisterRequest.java` | Registration DTO |
| `dto/LoginRequest.java` | Login DTO |
| `dto/AuthResponse.java` | JWT response DTO |
| `dto/CustomerResponse.java` | Profile response DTO |
| `repository/CustomerRepository.java` | Spring Data JPA |
| `security/JwtService.java` | JWT create/validate |
| `security/SecurityConfig.java` | Spring Security config |
| `service/AuthService.java` | Register/login logic |
| `service/CustomerService.java` | Profile CRUD |
| `controller/AuthController.java` | `/api/v1/auth/**` |
| `controller/CustomerController.java` | `/api/v1/customers/**` |
| `event/UserRegisteredEvent.java` | Kafka event POJO |
| `event/UserEventProducer.java` | Kafka publisher |
| `mapper/CustomerMapper.java` | MapStruct mapper |
| `src/main/resources/application.yml` | DB, Kafka, Eureka config |
| `Dockerfile` | Container definition |

### catalog-service
| File | Purpose |
|------|---------|
| `pom.xml` | Spring Boot, MongoDB, Kafka, MapStruct |
| `document/Product.java` | MongoDB document |
| `document/ProductCategory.java` | Category document |
| `document/ProductReview.java` | Embedded review |
| `dto/*.java` | Request/response DTOs |
| `repository/ProductRepository.java` | Spring Data MongoDB |
| `repository/CategoryRepository.java` | Spring Data MongoDB |
| `service/ProductService.java` | Product CRUD + search |
| `service/CategoryService.java` | Category CRUD |
| `service/ReviewService.java` | Review add/list |
| `controller/ProductController.java` | `/api/v1/products/**` |
| `controller/CategoryController.java` | `/api/v1/categories/**` |
| `event/ReviewAddedEvent.java` | Kafka event |
| `event/ReviewEventProducer.java` | Kafka publisher |
| `mapper/*.java` | MapStruct mappers |
| `src/main/resources/application.yml` | MongoDB, Eureka, Kafka |
| `Dockerfile` | Container definition |

### cart-service
| File | Purpose |
|------|---------|
| `pom.xml` | Spring Boot, Redis, OpenFeign |
| `model/Cart.java` | Redis-serializable model |
| `model/CartItem.java` | Cart item model |
| `dto/*.java` | Request/response DTOs |
| `client/CatalogServiceClient.java` | FeignClient → catalog |
| `service/CartService.java` | Add/remove/clear/checkout |
| `controller/CartController.java` | `/api/v1/carts/**` |
| `src/main/resources/application.yml` | Redis, Eureka, Feign |
| `Dockerfile` | Container definition |

### wishlist-service
| File | Purpose |
|------|---------|
| `pom.xml` | Spring Boot, JPA, OpenFeign |
| `entity/Wishlist.java` | JPA entity |
| `entity/WishlistItem.java` | JPA entity |
| `dto/*.java` | Request/response DTOs |
| `client/CatalogServiceClient.java` | FeignClient → catalog |
| `repository/WishlistRepository.java` | Spring Data JPA |
| `service/WishlistService.java` | Add/remove/list |
| `controller/WishlistController.java` | `/api/v1/wishlists/**` |
| `src/main/resources/application.yml` | PG, Eureka, Feign |
| `Dockerfile` | Container definition |

---

## Port Allocation

| Service | Port |
|---------|------|
| API Gateway | 8080 |
| User Service | 8081 |
| Catalog Service | 8082 |
| Cart Service | 8083 |
| Wishlist Service | 8084 |
| Config Server | 8888 |
| Discovery Server (Eureka) | 8761 |
| MySQL (user-service) | 3306 |
| PostgreSQL (wishlist-service) | 5432 |
| MongoDB (catalog-service) | 27017 |
| Redis (cart-service) | 6379 |
| Kafka | 9092 |
| Zookeeper | 2181 |
| Kafka UI | 8090 |

---

## Technology Versions

| Technology | Version |
|-----------|---------|
| Java | 21 |
| Spring Boot | 3.4.1 |
| Spring Cloud | 2024.0.0 |
| Spring Security | 6.x (via Boot) |
| JJWT | 0.12.6 |
| MapStruct | 1.6.0 |
| Lombok | latest (via Spring BOM) |
| Maven | 3.9.x |

---

## Key Design Decisions

1. **JWT at the Gateway** — The API Gateway validates JWT tokens centrally. Individual services trust the `X-User-Id` / `X-User-Email` headers injected by the gateway.
2. **MongoDB for Catalog** — Products have highly variable attributes; MongoDB's schema-less documents are ideal.
3. **Redis for Cart** — Carts are ephemeral, high-frequency read/write; Redis provides O(1) speed with TTL support.
4. **Kafka for async events** — User registration triggers welcome email events; review submission triggers notification events.
5. **Config Server with native backend** — YAML files bundled within the Config Server jar for easy local development (can swap to Git backend in production).
6. **FeignClient with Load Balancer** — Cart and Wishlist services use `@FeignClient` with `lb://catalog-service` for product validation.

---

## Verification Plan

### Automated
- Each service has its own Maven build: `mvn clean install -DskipTests`
- Full stack: `docker-compose up --build`

### Manual API Testing
- `POST /api/v1/auth/register` → get JWT
- `POST /api/v1/auth/login` → validate JWT
- `GET /api/v1/products` → catalog
- `POST /api/v1/carts/{userId}/items` → add to cart
- `POST /api/v1/wishlists/{userId}/items` → add to wishlist
- Eureka dashboard: `http://localhost:8761`
- Kafka UI: `http://localhost:8090`

---

## Open Questions

> [!IMPORTANT]
> **Q1: Database credentials** — Should I use placeholder/example credentials (e.g., `daraz_user/daraz_pass`) in `docker-compose.yml` and config files, or do you have specific values you'd like used?

> [!IMPORTANT]
> **Q2: JWT Secret** — Should I generate a random base64 secret key and embed it, or leave it as an environment variable placeholder (`${JWT_SECRET}`)?

> [!NOTE]
> **Q3: Kafka topics** — I'll create two topics by default: `user-events` and `catalog-events`. Let me know if you need additional topics.

> [!NOTE]
> **Q4: Email/Notification service** — The architecture document mentions a future Notification Service for async emails. Should I include Kafka consumer stubs for this in the current implementation?
