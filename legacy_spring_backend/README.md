# HariMart Backend — Spring Boot 3 REST API

The backend for **HariMart**, an Indian e-commerce web platform. Built with **Java 17**, **Spring Boot 3.3.0**, **Spring Data JPA**, **Spring Security 6**, and **JWT** stateless token authentication.

---

## Architecture Overview

- **Architecture Layering**: Controller &rarr; Service &rarr; Repository &rarr; Database
- **Security**: Spring Security filter chain with custom `JwtAuthenticationFilter`
- **Password Hashing**: BCrypt with strength factor 10
- **Database Support**:
  - **MySQL 8.x** (default for production / local database server)
  - **H2 File-Backed** (development profile `./data/harimart` for zero-install dev)
- **CORS**: Enabled with `allowedOriginPatterns("*")` and `allowCredentials(true)` for standalone frontends on ports `5500`, `3000`, etc.
- **Data Seeding**: Automatic via `DataInitializer.java` on first boot (32 products across 8 categories, demo users, orders, reviews).

---

## Directory Layout

```
backend/
├── pom.xml
├── README.md
├── src/
│   ├── main/
│   │   ├── java/com/harimart/
│   │   │   ├── config/             # Security, Data Initializer, Audit config
│   │   │   ├── controller/         # REST API Controllers (/api/**)
│   │   │   ├── dto/                # Request / Response DTOs
│   │   │   ├── entity/             # JPA Entities (User, Product, Order, Cart, etc.)
│   │   │   ├── enums/              # Role, OrderStatus, PaymentMethod, etc.
│   │   │   ├── exception/          # Global Exception Handler & Custom Exceptions
│   │   │   ├── repository/         # Spring Data JPA Repositories
│   │   │   ├── security/           # JWT Provider, Auth Filter, UserDetailsService
│   │   │   ├── service/            # Business Service Interfaces
│   │   │   │   └── impl/           # Service Implementations
│   │   │   └── HariMartApplication.java
│   │   └── resources/
│   │       ├── application.yml         # Base configuration (JWT, shipping, promo)
│   │       ├── application-dev.yml     # Dev profile with H2 DB
│   │       ├── application-mysql.yml   # MySQL profile (localhost:3306/harimart_db)
│   │       ├── schema.sql              # MySQL DDL script
│   │       └── data.sql                # Seed SQL script
│   └── test/
│       └── java/com/harimart/          # Unit and Integration Tests
```

---

## Prerequisites

1. **Java JDK 17+** (Eclipse Adoptium OpenJDK 17 or Oracle JDK 17)
2. **Apache Maven 3.8+**
3. **MySQL Server 8.x** (optional if using the `dev` profile)

---

## Database Configuration

### 1. MySQL Setup (Default / Production)

Log into your local MySQL server and create the database:

```sql
CREATE DATABASE IF NOT EXISTS harimart_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Check `src/main/resources/application-mysql.yml` for connection settings:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/harimart_db?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
    driverClassName: com.mysql.cj.jdbc.Driver
    username: root
    password: password
```
*Note: Update `username` and `password` if your local MySQL configuration differs.*

### 2. H2 Fallback (Dev Profile)

If MySQL is not installed locally, you can use the file-backed H2 profile. The database is saved to `./data/harimart.mv.db`:
```yaml
spring:
  datasource:
    url: jdbc:h2:file:./data/harimart;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE
    driverClassName: org.h2.Driver
    username: sa
    password: ""
```
H2 Console is accessible at `http://localhost:8080/h2-console`.

---

## How to Build & Run

### 1. Build and Run Tests

```bash
# In the backend/ folder:
mvn clean test
```

### 2. Run with MySQL Profile

```bash
# Using Maven:
mvn spring-boot:run "-Dspring-boot.run.profiles=mysql"

# Or package into a JAR and run:
mvn clean package -DskipTests
java -jar -Dspring.profiles.active=mysql target/harimart-backend-1.0.0.jar
```

### 3. Run with H2 Dev Profile

```bash
mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
```

The server will start on **`http://localhost:8080`**.

---

## Seed Accounts & Credentials

| Role | Email | Password | Access |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin@harimart.com` | `admin123` | Storefront & Admin Portal (`/admin.html`) |
| **Customer** | `customer@harimart.com` | `customer123` | Storefront, Cart, Checkout, Profile |
| **Customer** | `priya@example.com` | `customer123` | Storefront, Cart, Checkout, Profile |

*Coupon Code*: **`HARI10`** (10% discount on orders).

---

## Key REST API Endpoints

All APIs are prefixed with `/api`.

### Authentication (`/api/auth`)
- `POST /api/auth/register` — Register a new customer account
- `POST /api/auth/login` — Login with email/phone and password, returns JWT token
- `GET /api/auth/me` — Retrieve current authenticated user profile

### Products & Categories (`/api/products`, `/api/categories`)
- `GET /api/products` — Filter products by category, search term, price range, rating, sort, pagination
- `GET /api/products/{id}` — Get full product details by ID
- `GET /api/products/{id}/related` — Get related products in the same category
- `POST /api/products/{id}/reviews` — Submit a review & rating
- `GET /api/categories` — List all active categories

### Shopping Cart (`/api/cart`)
- `GET /api/cart` — Fetch user's cart (with coupon calculation if applicable)
- `POST /api/cart/items` — Add product to cart
- `PUT /api/cart/items/{itemId}` — Update item quantity
- `DELETE /api/cart/items/{itemId}` — Remove item from cart
- `DELETE /api/cart` — Clear entire cart

### Orders (`/api/orders`)
- `POST /api/orders` — Place order from cart or direct buy
- `GET /api/orders` — List logged-in user's order history
- `GET /api/orders/{id}` — Order details by ID
- `GET /api/orders/track/{orderNumber}` — Public/User order tracking by order number (e.g. `HM-2026-XXXXX`)
- `POST /api/orders/{id}/cancel` — Cancel an order

### Admin Operations (`/api/admin`, `/api/products`, `/api/orders`, `/api/users`)
- `GET /api/admin/dashboard` — Metric counters (revenue, orders, users, low-stock items)
- `POST /api/products` — Create new product
- `PUT /api/products/{id}` — Update existing product
- `DELETE /api/products/{id}` — Delete or soft-delete product
- `GET /api/admin/orders` — Manage all store orders
- `PUT /api/orders/{id}/status` — Update order status (`PROCESSING`, `SHIPPED`, `DELIVERED`, `CANCELLED`)
- `GET /api/users` — List registered customers
- `PUT /api/users/{id}/status` — Toggle user active/blocked status
