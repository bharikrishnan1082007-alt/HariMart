<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HariMart — Discover More. Shop Smarter.</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/responsive.css">
</head>
<body>

    <jsp:include page="includes/header.jsp"/>

    <!-- Hero Showcase Section -->
    <section class="hero-section">
        <div class="container">
            <div class="hero-grid">
                <div>
                    <div class="hero-tagline-badge">
                        <span>✨</span> Discover More. Shop Smarter.
                    </div>
                    <h1 class="hero-title">
                        Festive Mega Deals & Everyday <span>Delights</span>
                    </h1>
                    <p class="hero-desc">
                        Explore thousands of curated products from top verified Indian merchants with guaranteed 100% authenticity and lightning-fast doorstep delivery.
                    </p>
                    <div class="hero-actions">
                        <a href="#featured-products" class="btn-primary">
                            <span>🛍️</span> Shop Now
                        </a>
                        <a href="${pageContext.request.contextPath}/category?slug=electronics-gadgets" class="btn-secondary">
                            <span>⚡</span> Top Electronics
                        </a>
                    </div>
                </div>
                <div>
                    <div class="hero-visual-card">
                        <img src="https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=800&auto=format&fit=crop" 
                             alt="Featured Electronics" class="hero-img">
                        <div style="position: absolute; bottom: 2rem; left: 2.5rem; background: rgba(15, 23, 42, 0.85); backdrop-filter: blur(8px); padding: 0.75rem 1.25rem; border-radius: 12px; border: 1px solid rgba(255,255,255,0.15);">
                            <div style="font-size: 0.75rem; color: #ffb547; font-weight: 700; text-transform: uppercase;">Trending Deal</div>
                            <div style="font-size: 1.1rem; font-weight: 800; color: #fff;">Flagship 5G Smartphones</div>
                            <div style="font-size: 0.95rem; font-weight: 700; color: #38bdf8;">From ₹45,999</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <main class="container" style="padding-top: 3.5rem;">

        <!-- Category Discovery Cards -->
        <div class="section-header">
            <div>
                <h2 class="section-title">Popular Categories</h2>
                <p class="section-subtitle">Browse handpicked collections curated for every lifestyle</p>
            </div>
        </div>

        <div class="categories-showcase">
            <c:forEach var="cat" items="${categories}">
                <a href="${pageContext.request.contextPath}/category?slug=${cat.slug}" class="category-card">
                    <img src="${cat.imageUrl}" alt="${cat.name}" class="category-card-img">
                    <div class="category-card-title">${cat.name}</div>
                    <div style="font-size: 0.8rem; color: var(--slate-500); margin-top: 0.25rem;">Explore items →</div>
                </a>
            </c:forEach>
        </div>

        <!-- Featured Products Grid -->
        <div class="section-header" id="featured-products">
            <div>
                <h2 class="section-title">Featured Products</h2>
                <p class="section-subtitle">Top-rated items hand-selected by our marketplace editors</p>
            </div>
            <a href="${pageContext.request.contextPath}/search" style="color: var(--navy-600); font-weight: 700; font-size: 0.95rem;">View All →</a>
        </div>

        <div class="products-grid">
            <c:forEach var="p" items="${featuredProducts}">
                <div class="product-card">
                    <div class="product-thumb-container">
                        <c:if test="${p.hasDiscount()}">
                            <span class="badge-discount">${p.discountPercentage}% OFF</span>
                        </c:if>
                        <button class="wishlist-btn wishlist-toggle-btn" data-product-id="${p.id}" data-context-path="${pageContext.request.contextPath}" title="Add to Wishlist">
                            ♥
                        </button>
                        <a href="${pageContext.request.contextPath}/product?id=${p.id}" style="width: 100%; height: 100%;">
                            <img src="${p.imageUrl}" alt="${p.name}" class="product-thumb">
                        </a>
                    </div>
                    <div class="product-content">
                        <div class="product-brand">${p.brand} • <span style="color: var(--navy-600);">${p.sellerStoreName}</span></div>
                        <a href="${pageContext.request.contextPath}/product?id=${p.id}" class="product-title">${p.name}</a>
                        <div class="product-rating">
                            <span>★</span> ${p.rating} <span>(${p.reviewCount})</span>
                        </div>
                        <div class="product-price-row">
                            <span class="price-current">${p.formattedPrice}</span>
                            <c:if test="${p.hasDiscount()}">
                                <span class="price-original">${p.formattedOriginalPrice}</span>
                            </c:if>
                        </div>
                        <div class="product-card-footer">
                            <button type="button" class="btn-add-cart" onclick="addToCartAjax(${p.id}, 1, '${pageContext.request.contextPath}')">
                                🛒 Add to Cart
                            </button>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>

        <!-- Bestsellers Section -->
        <div class="section-header">
            <div>
                <h2 class="section-title">Bestselling Items in Bharat</h2>
                <p class="section-subtitle">Highest volume and most-reviewed essentials</p>
            </div>
        </div>

        <div class="products-grid">
            <c:forEach var="p" items="${bestsellers}">
                <div class="product-card">
                    <div class="product-thumb-container">
                        <c:if test="${p.hasDiscount()}">
                            <span class="badge-discount">${p.discountPercentage}% OFF</span>
                        </c:if>
                        <button class="wishlist-btn wishlist-toggle-btn" data-product-id="${p.id}" data-context-path="${pageContext.request.contextPath}" title="Add to Wishlist">
                            ♥
                        </button>
                        <a href="${pageContext.request.contextPath}/product?id=${p.id}" style="width: 100%; height: 100%;">
                            <img src="${p.imageUrl}" alt="${p.name}" class="product-thumb">
                        </a>
                    </div>
                    <div class="product-content">
                        <div class="product-brand">${p.brand}</div>
                        <a href="${pageContext.request.contextPath}/product?id=${p.id}" class="product-title">${p.name}</a>
                        <div class="product-rating">
                            <span>★</span> ${p.rating} <span>(${p.reviewCount})</span>
                        </div>
                        <div class="product-price-row">
                            <span class="price-current">${p.formattedPrice}</span>
                            <c:if test="${p.hasDiscount()}">
                                <span class="price-original">${p.formattedOriginalPrice}</span>
                            </c:if>
                        </div>
                        <div class="product-card-footer">
                            <button type="button" class="btn-add-cart" onclick="addToCartAjax(${p.id}, 1, '${pageContext.request.contextPath}')">
                                🛒 Add to Cart
                            </button>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>

        <!-- New Arrivals Section -->
        <div class="section-header">
            <div>
                <h2 class="section-title">New Arrivals</h2>
                <p class="section-subtitle">Just dropped by our verified merchant community</p>
            </div>
        </div>

        <div class="products-grid">
            <c:forEach var="p" items="${newArrivals}">
                <div class="product-card">
                    <div class="product-thumb-container">
                        <button class="wishlist-btn wishlist-toggle-btn" data-product-id="${p.id}" data-context-path="${pageContext.request.contextPath}" title="Add to Wishlist">
                            ♥
                        </button>
                        <a href="${pageContext.request.contextPath}/product?id=${p.id}" style="width: 100%; height: 100%;">
                            <img src="${p.imageUrl}" alt="${p.name}" class="product-thumb">
                        </a>
                    </div>
                    <div class="product-content">
                        <div class="product-brand">${p.brand}</div>
                        <a href="${pageContext.request.contextPath}/product?id=${p.id}" class="product-title">${p.name}</a>
                        <div class="product-rating">
                            <span>★</span> ${p.rating} <span>(${p.reviewCount})</span>
                        </div>
                        <div class="product-price-row">
                            <span class="price-current">${p.formattedPrice}</span>
                            <c:if test="${p.hasDiscount()}">
                                <span class="price-original">${p.formattedOriginalPrice}</span>
                            </c:if>
                        </div>
                        <div class="product-card-footer">
                            <button type="button" class="btn-add-cart" onclick="addToCartAjax(${p.id}, 1, '${pageContext.request.contextPath}')">
                                🛒 Add to Cart
                            </button>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>

    </main>

    <jsp:include page="includes/footer.jsp"/>

</body>
</html>
