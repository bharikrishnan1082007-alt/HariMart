# HariMart — Complete Multi-Vendor E-Commerce Platform

> **Discover More. Shop Smarter.**  
> India's trusted multi-vendor e-commerce destination connecting local merchants, artisans, and happy shoppers.

HariMart is a full-featured, production-ready Amazon-like Multi-Vendor E-Commerce Web Application built using **Java Servlets (Java 17)**, **JSP**, **JSTL**, **JDBC (HikariCP)**, and an embedded/file-backed **H2 Database**, fully packaged as a deployable WAR (`HariMart.war`) on **Apache Tomcat 9**.

---

## 🚀 Live Local Deployment

- **Application URL**: [http://localhost:8080/HariMart/](http://localhost:8080/HariMart/)
- **H2 Database Console**: [http://localhost:8080/HariMart/h2-console](http://localhost:8080/HariMart/h2-console) (JDBC URL: `jdbc:h2:file:./data/harimart_db`)

### Seed Demo Accounts

| Role | Email | Password | Description |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin@harimart.com` | `admin123` | Full governance: users, sellers, products moderation, GMV metrics |
| **Seller 1** | `seller1@harimart.com` | `seller123` | Apex Retail India: inventory listing, stock toggle, fulfillment |
| **Seller 2** | `seller2@harimart.com` | `seller123` | Bharat Global Traders: catalog management, earnings breakdown |
| **Buyer** | `buyer@harimart.com` | `buyer123` | Rahul Sharma: addresses, wishlist, cart, orders, live tracking |

---

## 🏗️ Architecture & Technology Stack

- **Runtime & Web Container**: Apache Tomcat 9.0.122 (`javax.servlet` API 4.0, JSP 2.3.3, JSTL 1.2)
- **Programming Language**: Java 17 (Adoptium Temurin OpenJDK 17)
- **Architecture**: MVC Pattern
  - **Controllers**: 14 Java Servlets with `@WebServlet` mappings
  - **Business Layer**: Clean domain services (`AuthService`, `ProductService`, `CartService`, `OrderService`, `SellerService`, `AdminService`)
  - **Data Access**: Normalized JDBC DAOs utilizing HikariCP 5.1.0 connection pooling
  - **Views**: 26 Server-Side JSP templates with modular header/footer taglibs inside `WEB-INF/views/`
  - **Security**: Role-based access control filter (`AuthFilter`) enforcing tenant separation between `BUYER`, `SELLER`, and `ADMIN`
- **Database**: H2 Database (File persistence in `./data/harimart_db` with automatic schema & seed data runner)
- **Frontend Assets**: Responsive CSS3 (Custom design system: Midnight Navy `#172554`, Vibrant Amber `#FFB547`, Ivory `#FAFAF7`), Vanilla ES6 JavaScript, AJAX Cart & Search autocomplete

---

## 📁 Project Directory Structure

```
d:/harimart/
├── pom.xml                                   # Maven configuration (WAR packaging, Java 17 release)
├── build.bat                                 # Automated clean compile, package & Tomcat deploy script
├── run-tomcat.bat                            # Quick Tomcat 9 startup script
├── src/
│   ├── main/
│   │   ├── java/com/harimart/
│   │   │   ├── controller/                   # 14 Servlet Controllers
│   │   │   │   ├── ShopServlet.java          # Home / Catalog page (/)
│   │   │   │   ├── ProductServlet.java       # Product details & reviews (/product)
│   │   │   │   ├── CategoryServlet.java      # Taxonomy category browsing (/category)
│   │   │   │   ├── SearchServlet.java        # Search & instant suggestions (/search)
│   │   │   │   ├── CartServlet.java          # Cart management & coupon discount (/cart)
│   │   │   │   ├── CheckoutServlet.java      # 3-step transactional checkout (/checkout)
│   │   │   │   ├── OrderServlet.java         # Order tracking & history (/orders)
│   │   │   │   ├── AuthServlet.java          # Login, Register, Logout (/login, /register, /logout)
│   │   │   │   ├── ProfileServlet.java       # User profile details (/profile)
│   │   │   │   ├── AddressServlet.java       # Multi-address manager (/addresses)
│   │   │   │   ├── WishlistServlet.java      # Wishlist operations (/wishlist)
│   │   │   │   ├── ReviewServlet.java        # Product review submissions (/reviews)
│   │   │   │   ├── SellerServlet.java        # Merchant Central (/seller/*)
│   │   │   │   └── AdminServlet.java         # Operations Console (/admin/*)
│   │   │   ├── dao/                          # 10 JDBC Data Access Objects
│   │   │   ├── service/                      # 6 Business Domain Services
│   │   │   ├── model/                        # 12 Entity & Transfer Models
│   │   │   ├── filter/                       # AuthFilter (RBAC) & EncodingFilter (UTF-8)
│   │   │   └── util/                         # HikariCP DatabaseManager, BCrypt PasswordUtil, CurrencyUtil
│   │   ├── resources/
│   │   │   ├── application.properties        # App & DB configuration
│   │   │   ├── schema.sql                    # 11 Normalized relational tables
│   │   │   └── data.sql                      # 22 Indian products, 5 categories, seed accounts
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   ├── web.xml                   # Deployment descriptor with error codes
│   │       │   └── views/                    # 26 JSP Views + Header/Footer includes
│   │       │       ├── includes/             # header.jsp, footer.jsp
│   │       │       ├── shop.jsp              # Home storefront
│   │       │       ├── product-detail.jsp    # Product page
│   │       │       ├── cart.jsp              # Cart page
│   │       │       ├── checkout.jsp          # Checkout page
│   │       │       ├── order-history.jsp     # Order history
│   │       │       ├── order-detail.jsp      # Tracking timeline
│   │       │       ├── seller-dashboard.jsp  # Seller Central
│   │       │       ├── seller-products.jsp   # Inventory manager
│   │       │       ├── seller-orders.jsp     # Fulfillment manager
│   │       │       ├── seller-analytics.jsp  # Commission accounting
│   │       │       ├── admin-dashboard.jsp   # Platform overview & GMV
│   │       │       ├── admin-users.jsp       # User accounts
│   │       │       ├── admin-sellers.jsp     # Merchant onboarding
│   │       │       ├── admin-products.jsp    # Catalog moderation
│   │       │       ├── admin-categories.jsp  # Categories management
│   │       │       └── error.jsp             # Friendly HTTP error page
│   │       └── assets/                       # CSS stylesheets & JS scripts
│   └── test/java/com/harimart/               # Automated unit tests
```

---

## 🛠️ Build & Run Commands

### 1. Build & Deploy with Maven
```powershell
# Run the automated build script:
.\build.bat

# Or manually via Maven:
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot"
C:\tools\apache-maven-3.9.6\bin\mvn.cmd clean package
Copy-Item target\HariMart.war C:\tools\apache-tomcat-9.0.122\webapps\HariMart.war -Force
```

### 2. Start Apache Tomcat
```powershell
# Run the startup script:
.\run-tomcat.bat

# Or via PowerShell daemon:
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot"
$env:CATALINA_HOME = "C:\tools\apache-tomcat-9.0.122"
& "C:\tools\apache-tomcat-9.0.122\bin\catalina.bat" run
```

---

## 🛒 Key Features & Capabilities

1. **Customer Front Store**:
   - Comprehensive Indian product catalog across 5 categories with ₹ INR formatting.
   - Live AJAX search bar with instant autocomplete dropdown.
   - Dynamic product detail pages with gallery zoom, stock count, and verified customer reviews.
   - Real-time cart slide-in notifications, quantity adjustments, and promotional coupon engine (`HARI10` for 10% off).
   - 3-step checkout with delivery address selection/creation and payment methods (COD, UPI, Card).
   - Visual order progress tracker (Placed → Confirmed → Packed → Shipped → Delivered).
   - Customer profile, saved delivery addresses manager, and wishlist.

2. **Merchant Central (`/seller/*`)**:
   - Store onboarding and registration workflow.
   - Inventory management: add, edit, restock, toggle availability, and delete listings.
   - Order fulfillment manager: update tracking states from `CONFIRMED` to `DELIVERED`.
   - Financial statement report: gross GMV, 10% platform commission deductions, and net payouts.

3. **Admin Operations Console (`/admin/*`)**:
   - Platform metrics: Total GMV, platform commission revenues, total orders, buyer and seller counts.
   - User account control: modify user roles (`BUYER`, `SELLER`, `ADMIN`) and toggle account active status.
   - Seller partner approval: review and approve merchant onboarding requests.
   - Catalog moderation: audit vendor listings and hide non-compliant products.
   - Category taxonomy management: create, edit, and organize marketplace categories.
