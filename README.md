# HariMart — Full-Stack E-Commerce Platform

> **Bharat Ka Apna Superstore** — Unbeatable Prices, Everyday Delights.

HariMart is a complete, production-grade full-stack e-commerce web platform inspired by the functional depth of Amazon and Meesho, built with an original brand identity, modern responsive UI, Spring Boot 3 Java backend, Spring Security with JWT, and MySQL/H2 dual database support.

The project is structured into **completely separate** `backend/` and `frontend/` directories, communicating strictly over RESTful JSON APIs.

---

## 📁 Project Structure

```
HariMart/
│
├── backend/
│   ├── pom.xml                               # Maven project descriptor
│   ├── README.md                             # Backend-specific architecture & API guide
│   └── src/
│       ├── main/
│       │   ├── java/com/harimart/
│       │   │   ├── HariMartApplication.java  # Spring Boot main entrypoint
│       │   │   ├── config/                   # SecurityConfig, DataInitializer, Auditing
│       │   │   ├── controller/               # REST API Controllers (/api/**)
│       │   │   ├── dto/                      # Request & Response DTOs
│       │   │   ├── entity/                   # JPA Entities (User, Product, Order, Cart, etc.)
│       │   │   ├── enums/                    # Enums (Role, OrderStatus, PaymentMethod, etc.)
│       │   │   ├── exception/                # Global exception handler & custom exceptions
│       │   │   ├── repository/               # Spring Data JPA Repositories & Specifications
│       │   │   ├── security/                 # JWT Provider, Auth Filter, UserDetailsService
│       │   │   └── service/                  # Business logic interfaces & implementations
│       │   │
│       │   └── resources/
│       │       ├── application.yml           # Base application configuration
│       │       ├── application-dev.yml       # H2 file-backed persistence (Zero-install dev)
│       │       ├── application-mysql.yml     # Local MySQL datasource
│       │       ├── schema.sql                # Complete SQL schema DDL
│       │       └── data.sql                  # Initial SQL seed data
│       │
│       └── test/java/com/harimart/           # Automated Unit & Security Integration Tests
│
├── frontend/                                 # Pure HTML5 / CSS3 / Vanilla JS Frontend
│   ├── index.html                            # Home page & hero carousel
│   ├── products.html                         # Catalog listing with filters & pagination
│   ├── product-detail.html                   # Product details, gallery & reviews
│   ├── cart.html                             # Shopping cart & coupon calculator
│   ├── checkout.html                         # 3-step checkout (address, payment, recap)
│   ├── order-tracking.html                   # Visual shipment progress tracking
│   ├── account.html                          # Customer account portal (orders, addresses, wishlist)
│   ├── login.html                            # Sign in with 1-click demo buttons
│   ├── register.html                         # Customer registration form
│   ├── admin.html                            # Admin management portal (KPIs, products, orders, users)
│   ├── css/
│   │   ├── style.css                         # Primary storefront styles & design system
│   │   ├── responsive.css                    # Mobile & tablet responsiveness rules
│   │   └── admin.css                         # Admin portal dashboard styles
│   └── js/
│       ├── api.js                            # Unified API client (API_BASE_URL = http://localhost:8080/api)
│       ├── auth.js                           # JWT authentication & session management
│       ├── cart.js                           # Cart actions & badge counters
│       ├── products.js                       # Catalog search & filtering controller
│       ├── orders.js                         # Order history & status progression controller
│       ├── admin.js                          # Admin dashboard & management controller
│       ├── main.js                           # Global UI helpers, toast notifications, modals
│       ├── home.js                           # Home page deals & category loaders
│       ├── product-detail.js                 # Product gallery, zoom, reviews, buy-now
│       ├── checkout.js                       # 3-step checkout logic & payment simulation
│       ├── account.js                        # Account profile, address book, wishlist
│       └── ui.js                             # UI utilities
│
└── README.md                                 # Full project documentation & run guide
```

---

## 💻 Tech Stack

| Layer | Technologies |
| :--- | :--- |
| **Backend** | Java 17, Spring Boot 3.3.4, Spring Data JPA, Spring Web, Bean Validation |
| **Security** | Spring Security 6, JWT (JJWT 0.12.5), BCrypt Password Hashing, Stateless Session |
| **Databases** | MySQL 8.x (Default / Production) & H2 File-backed (Development / Zero-Install Fallback) |
| **Frontend** | HTML5, CSS3 (Custom Design System, Flexbox, CSS Grid), Vanilla JavaScript (ES6+), Fetch API |
| **Build Tool** | Apache Maven 3.9+ |

---

## 🚀 How to Run HariMart

### 1. Prerequisites
- **Java 17** or higher (`java -version`)
- **Apache Maven 3.8+** (`mvn -v`)
- **MySQL Server 8.x** running on `localhost:3306` (or use the H2 `dev` profile)
- **VS Code** with **Live Server** extension (or any local static server / browser)

---

### 2. Start the Backend

Open a terminal and navigate to the `backend/` directory:

```bash
cd backend
```

#### Option A: Running with MySQL (Default)
1. Ensure MySQL is running on `localhost:3306`.
2. Create the database:
   ```sql
   CREATE DATABASE IF NOT EXISTS harimart_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
3. Default credentials in `application-mysql.yml` are `root` / `password`. Modify if needed.
4. Run:
   ```bash
   mvn spring-boot:run "-Dspring-boot.run.profiles=mysql"
   ```

#### Option B: Running with H2 File-Backed Profile (Zero-Install Fallback)
If you don't have MySQL installed, run with the development profile. It persists data automatically to `./data/harimart`:
```bash
mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
```

The backend server will start on **`http://localhost:8080`**.
*(H2 Console is available at `http://localhost:8080/h2-console` with JDBC URL `jdbc:h2:file:./data/harimart`, user `sa`, blank password).*

---

### 3. Launch the Frontend

The frontend is completely independent and connects to `http://localhost:8080/api` via REST.

#### Using VS Code Live Server:
1. In VS Code, open the workspace `d:\harimart`.
2. Right-click `frontend/index.html` and select **"Open with Live Server"**.
3. The storefront will open in your browser at `http://localhost:5500` (or `http://127.0.0.1:5500`).

#### Using Python HTTP Server:
```bash
cd frontend
python -m http.server 3000
```
Then visit `http://localhost:3000`.

#### Direct File Open:
You can also open `frontend/index.html` directly in your browser.

---

## 🔑 Demo Credentials

| Role | Email | Password | Access Capabilities |
| :--- | :--- | :--- | :--- |
| **Administrator** | `admin@harimart.com` | `admin123` | Storefront & Admin Portal (`admin.html`), product/category CRUD, order status updating, user moderation, review moderation |
| **Customer** | `customer@harimart.com` | `customer123` | Storefront, cart, checkout, order tracking, address book, wishlist, review submission |
| **Customer** | `priya@example.com` | `customer123` | Alternate customer profile |

> 💡 *Tip: On `login.html`, click either **"Demo Customer"** or **"Demo Admin"** to auto-populate credentials and sign in with a single click!*

*Coupon Code*: **`HARI10`** gives an instant 10% discount on order subtotal!

---

## 🧪 Running Automated Tests

Run the full suite of unit and integration tests from the `backend/` folder:

```bash
cd backend
mvn clean test
```

**Results**: 14 tests run, 0 failures, 0 errors, 0 skipped (`BUILD SUCCESS`).

Test coverage includes:
- Spring Boot ApplicationContext bootstrap
- User registration, duplicate validation, and BCrypt password encryption
- Product catalog dynamic multi-attribute filtering, search, and pagination
- Shopping cart pricing calculations, stock constraints, and coupon application
- Order checkout with atomic stock deduction, coupon calculation, and cancellation rules
- Spring Security role-based authorization (401 Unauthorized, 403 Forbidden on Admin endpoints)

---

## 📋 Key REST API Endpoints

All APIs are served under the `/api` prefix:

### Authentication (`/api/auth`)
- `POST /api/auth/register` — Register a new customer
- `POST /api/auth/login` — Sign in and obtain JWT Bearer token
- `GET  /api/auth/me` — Retrieve authenticated user profile

### Products & Categories (`/api/products`, `/api/categories`)
- `GET    /api/products` — Filter products (`search`, `category`, `brand`, `minPrice`, `maxPrice`, `rating`, `inStock`, `sort`, `page`, `size`)
- `GET    /api/products/{id}` — Get product details with gallery images and rating
- `GET    /api/products/featured` — Get featured products
- `GET    /api/products/trending` — Get trending products
- `GET    /api/products/bestsellers` — Get best-selling products
- `GET    /api/products/{id}/related` — Get related products
- `POST   /api/products` — *(Admin)* Create new product
- `PUT    /api/products/{id}` — *(Admin)* Update product
- `DELETE /api/products/{id}` — *(Admin)* Deactivate product
- `GET    /api/categories` — List all active categories

### Shopping Cart (`/api/cart`)
- `GET    /api/cart?coupon=...` — Retrieve current user's cart with totals
- `POST   /api/cart/items` — Add product to cart with quantity
- `PUT    /api/cart/items/{id}` — Update item quantity
- `DELETE /api/cart/items/{id}` — Remove item from cart
- `DELETE /api/cart` — Clear entire cart

### Orders & Tracking (`/api/orders`)
- `POST   /api/orders` — Place order from cart or direct buy-now
- `GET    /api/orders` — Get authenticated customer's order history
- `GET    /api/orders/{id}` — Get order details
- `GET    /api/orders/track/{orderNumber}` — Public/User order tracking by order number
- `PUT    /api/orders/{id}/status` — *(Admin)* Update order progression status
- `POST   /api/orders/{id}/cancel` — Cancel order (if not yet shipped)

### Customer Account & Reviews (`/api/users`, `/api/reviews`)
- `GET    /api/users/addresses` — List user's saved addresses
- `POST   /api/users/addresses` — Save new address
- `DELETE /api/users/addresses/{id}` — Delete address
- `GET    /api/wishlist` — List user's wishlist
- `POST   /api/wishlist/{productId}` — Add/toggle wishlist item
- `DELETE /api/wishlist/{productId}` — Remove wishlist item
- `GET    /api/products/{id}/reviews` — Get product reviews
- `POST   /api/products/{id}/reviews` — Submit review (Rating 1–5 + comment)

### Admin Operations (`/api/admin`)
- `GET /api/admin/dashboard` — Overview metrics (revenue, orders, users, products, low stock)
- `GET /api/admin/orders` — All store orders for admin management
- `GET /api/users` — List registered users
- `PUT /api/users/{id}/status` — Toggle user Active / Inactive
- `GET /api/reviews` — All reviews for moderation
- `DELETE /api/reviews/{id}` — Remove review

---

## 🛡️ Business Rules Implemented
1. **Server-Side Pricing**: Prices and discounts are never trusted from the client; calculations are performed on the server using `BigDecimal`.
2. **Promotional Coupon**: Code `HARI10` grants an instant 10% discount off items subtotal.
3. **Delivery Charges**: Orders with subtotal &ge; ₹499 qualify for **FREE Delivery**; otherwise a ₹49 delivery charge applies.
4. **Stock Reservation & Overselling Prevention**: Quantities are verified during cart additions and atomically deducted upon checkout inside `@Transactional`. If an item exceeds stock, the order is rejected.
5. **Order Cancellation**: Customers can cancel an order while in `PLACED` or `CONFIRMED` status. Once `SHIPPED`, cancellation is disallowed. When cancelled, inventory stock is automatically restored.
6. **Data Isolation**: Customers can only view and modify their own carts, orders, addresses, and wishlists.
