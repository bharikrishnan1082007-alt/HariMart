<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- Top Notification Bar -->
<div style="background-color: #0f172a; color: #fcd34d; font-size: 0.8rem; font-weight: 600; text-align: center; padding: 0.35rem 1rem; letter-spacing: 0.02em;">
    ⚡ <strong>SUPER SAVER DEALS:</strong> Use promo code <span style="background: rgba(255,181,71,0.25); color: #fff; padding: 0.1rem 0.4rem; border-radius: 4px; font-weight: 700;">HARI10</span> for 10% Instant Discount! Free shipping across India on orders over ₹499.
</div>

<!-- Main Header -->
<header class="site-header">
    <div class="container">
        <div class="header-top">
            <!-- HariMart Custom Logo with curved shopping smile/arrow -->
            <a href="${pageContext.request.contextPath}/" class="brand-logo" title="HariMart Home">
                <div class="logo-symbol">H</div>
                <div class="brand-text">
                    <div class="brand-name">Hari<span>Mart</span></div>
                    <!-- Custom SVG Smile/Arrow Mark -->
                    <svg class="brand-curve-arrow" viewBox="0 0 100 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M 5 5 Q 50 20 95 6" stroke="#FFB547" stroke-width="3.5" stroke-linecap="round"/>
                        <path d="M 85 4 L 96 6 L 90 15" stroke="#FFB547" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                </div>
            </a>

            <!-- Delivery Location -->
            <div class="location-badge" onclick="showToast('Delivering across all pincodes in India with Express Delivery.', 'success')">
                <span style="font-size: 1.25rem;">📍</span>
                <div>
                    <div style="font-size: 0.72rem; color: #94a3b8; line-height: 1;">Deliver to</div>
                    <div style="font-weight: 700; color: #ffffff; font-size: 0.82rem;">India, 560001</div>
                </div>
            </div>

            <!-- Search Bar with Category Select & Autocomplete -->
            <form action="${pageContext.request.contextPath}/search" method="GET" class="header-search-form" id="headerSearchForm">
                <select name="category" class="search-category-select">
                    <option value="">All Categories</option>
                    <c:forEach var="cat" items="${applicationScope.globalCategories != null ? applicationScope.globalCategories : categories}">
                        <option value="${cat.id}" ${param.category == cat.id ? 'selected' : ''}>${cat.name}</option>
                    </c:forEach>
                </select>
                <input type="text" name="q" id="headerSearchInput" class="search-input" 
                       placeholder="Search products, brands, essentials..." 
                       value="${param.q}" autocomplete="off" data-context-path="${pageContext.request.contextPath}">
                <button type="submit" class="search-submit-btn">
                    <span>🔍</span>
                    <span>Search</span>
                </button>
                <div id="searchAutocompleteDropdown" class="autocomplete-dropdown"></div>
            </form>

            <!-- Actions: Account, Wishlist, Cart -->
            <div class="header-actions">
                <!-- User Account Menu -->
                <div class="account-dropdown-wrapper">
                    <c:choose>
                        <c:when test="${not empty sessionScope.currentUser}">
                            <div class="header-btn">
                                <span style="font-size: 1.25rem;">👤</span>
                                <div>
                                    <div style="font-size: 0.72rem; color: #94a3b8; line-height: 1;">Hello, ${sessionScope.currentUser.name}</div>
                                    <div style="font-weight: 700; font-size: 0.85rem;">Account ▾</div>
                                </div>
                            </div>
                            <div class="account-menu">
                                <div style="padding: 0.5rem 1rem; font-size: 0.8rem; color: #64748b; border-bottom: 1px solid #e2e8f0;">
                                    Signed in as <strong>${sessionScope.currentUser.role}</strong>
                                </div>
                                <a href="${pageContext.request.contextPath}/profile">👤 My Profile</a>
                                <a href="${pageContext.request.contextPath}/orders">📦 My Orders</a>
                                <a href="${pageContext.request.contextPath}/addresses">📍 Delivery Addresses</a>
                                <a href="${pageContext.request.contextPath}/wishlist">❤️ Saved Wishlist</a>
                                
                                <c:if test="${sessionScope.currentUser.role == 'SELLER' || sessionScope.currentUser.role == 'ADMIN'}">
                                    <div class="account-menu-divider"></div>
                                    <a href="${pageContext.request.contextPath}/seller/dashboard" style="color: #2563eb; font-weight: 600;">🏪 Seller Central</a>
                                </c:if>

                                <c:if test="${sessionScope.currentUser.role == 'BUYER'}">
                                    <div class="account-menu-divider"></div>
                                    <a href="${pageContext.request.contextPath}/seller/register" style="color: #f59e0b; font-weight: 600;">🚀 Become a Seller</a>
                                </c:if>

                                <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                                    <div class="account-menu-divider"></div>
                                    <a href="${pageContext.request.contextPath}/admin/dashboard" style="color: #7c3aed; font-weight: 600;">🛡️ Admin Console</a>
                                </c:if>

                                <div class="account-menu-divider"></div>
                                <a href="${pageContext.request.contextPath}/logout" style="color: #e11d48; font-weight: 600;">🚪 Sign Out</a>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/login" class="header-btn">
                                <span style="font-size: 1.25rem;">👤</span>
                                <div>
                                    <div style="font-size: 0.72rem; color: #94a3b8; line-height: 1;">Hello, Sign In</div>
                                    <div style="font-weight: 700; font-size: 0.85rem;">Account & Lists</div>
                                </div>
                            </a>
                            <div class="account-menu">
                                <div style="padding: 0.75rem 1rem; text-align: center;">
                                    <a href="${pageContext.request.contextPath}/login" class="btn-primary" style="display: block; text-align: center; padding: 0.5rem; font-size: 0.85rem;">Sign In</a>
                                    <div style="font-size: 0.78rem; margin-top: 0.5rem; color: #64748b;">
                                        New customer? <a href="${pageContext.request.contextPath}/register" style="color: #2563eb; font-weight: 600;">Start here.</a>
                                    </div>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Wishlist Link -->
                <a href="${pageContext.request.contextPath}/wishlist" class="header-btn" title="Wishlist">
                    <span style="font-size: 1.25rem;">❤️</span>
                    <span class="badge-pill" id="wishlistCountBadge" style="${wishlistCount > 0 ? '' : 'display:none;'}">${wishlistCount}</span>
                    <span style="font-weight: 600; font-size: 0.85rem; display: none; @media(min-width: 900px){display: inline;}">Wishlist</span>
                </a>

                <!-- Shopping Cart Link -->
                <a href="${pageContext.request.contextPath}/cart" class="header-btn" title="Shopping Cart">
                    <span style="font-size: 1.35rem;">🛒</span>
                    <span class="badge-pill" id="cartCountBadge" style="${cartCount > 0 ? '' : 'display:none;'}">${cartCount}</span>
                    <span style="font-weight: 700; font-size: 0.85rem;">Cart</span>
                </a>
            </div>
        </div>
    </div>

    <!-- Category Ribbon Navbar -->
    <nav class="cat-navbar">
        <div class="container">
            <ul class="cat-nav-list">
                <li><a href="${pageContext.request.contextPath}/" class="cat-nav-link ${pageContext.request.requestURI == pageContext.request.contextPath || pageContext.request.requestURI == pageContext.request.contextPath.concat('/') ? 'active' : ''}">⚡ All Deals</a></li>
                <li><a href="${pageContext.request.contextPath}/category?slug=electronics-gadgets" class="cat-nav-link">📱 Electronics & Gadgets</a></li>
                <li><a href="${pageContext.request.contextPath}/category?slug=fashion-apparel" class="cat-nav-link">👕 Fashion & Apparel</a></li>
                <li><a href="${pageContext.request.contextPath}/category?slug=home-kitchen" class="cat-nav-link">☕ Home & Kitchen</a></li>
                <li><a href="${pageContext.request.contextPath}/category?slug=books-stationery" class="cat-nav-link">📚 Books & Stationery</a></li>
                <li><a href="${pageContext.request.contextPath}/category?slug=sports-fitness-outdoor" class="cat-nav-link">🏋️ Sports & Fitness</a></li>
                <c:if test="${not empty sessionScope.currentUser && sessionScope.currentUser.role == 'SELLER'}">
                    <li style="margin-left: auto;"><a href="${pageContext.request.contextPath}/seller/dashboard" class="cat-nav-link" style="color: var(--amber-400); font-weight: 700;">🏪 Merchant Dashboard</a></li>
                </c:if>
                <c:if test="${not empty sessionScope.currentUser && sessionScope.currentUser.role == 'ADMIN'}">
                    <li style="margin-left: auto;"><a href="${pageContext.request.contextPath}/admin/dashboard" class="cat-nav-link" style="color: #a78bfa; font-weight: 700;">🛡️ Admin Dashboard</a></li>
                </c:if>
            </ul>
        </div>
    </nav>
</header>
