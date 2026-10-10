<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<footer class="site-footer">
    <div class="container">
        <!-- Trust Badges -->
        <div class="footer-features">
            <div class="feature-box">
                <div class="feature-icon">🛡️</div>
                <div class="feature-text">
                    <h4>100% Original Products</h4>
                    <p>Guaranteed authentic by verified sellers across India</p>
                </div>
            </div>
            <div class="feature-box">
                <div class="feature-icon">🚚</div>
                <div class="feature-text">
                    <h4>Free Shipping Above ₹499</h4>
                    <p>Express doorstep delivery across all Indian pincodes</p>
                </div>
            </div>
            <div class="feature-box">
                <div class="feature-icon">🔄</div>
                <div class="feature-text">
                    <h4>7 Days Hassle-Free Return</h4>
                    <p>Easy returns & instant replacement guarantee</p>
                </div>
            </div>
            <div class="feature-box">
                <div class="feature-icon">💳</div>
                <div class="feature-text">
                    <h4>100% Secure Payments</h4>
                    <p>UPI, Credit/Debit cards & Cash on Delivery</p>
                </div>
            </div>
        </div>

        <!-- Footer Columns -->
        <div class="footer-grid">
            <div class="footer-col">
                <div class="brand-name" style="font-size: 1.4rem; color: #fff; margin-bottom: 0.75rem;">
                    Hari<span style="color: var(--amber-400);">Mart</span>
                </div>
                <p style="font-size: 0.9rem; line-height: 1.6; color: var(--slate-400); max-width: 320px;">
                    Discover More. Shop Smarter. India's trusted multi-vendor e-commerce destination connecting local artisans, top merchants, and happy shoppers.
                </p>
            </div>
            <div class="footer-col">
                <h5>Categories</h5>
                <ul class="footer-links">
                    <li><a href="${pageContext.request.contextPath}/category?slug=electronics-gadgets">Electronics & Gadgets</a></li>
                    <li><a href="${pageContext.request.contextPath}/category?slug=fashion-apparel">Fashion & Apparel</a></li>
                    <li><a href="${pageContext.request.contextPath}/category?slug=home-kitchen">Home & Kitchen</a></li>
                    <li><a href="${pageContext.request.contextPath}/category?slug=books-stationery">Books & Stationery</a></li>
                    <li><a href="${pageContext.request.contextPath}/category?slug=sports-fitness-outdoor">Sports & Fitness</a></li>
                </ul>
            </div>
            <div class="footer-col">
                <h5>Sell on HariMart</h5>
                <ul class="footer-links">
                    <li><a href="${pageContext.request.contextPath}/seller/register">Become a Verified Seller</a></li>
                    <li><a href="${pageContext.request.contextPath}/seller/dashboard">Seller Central Dashboard</a></li>
                    <li><a href="${pageContext.request.contextPath}/seller/orders">Fulfillment Services</a></li>
                    <li><a href="${pageContext.request.contextPath}/seller/analytics">Fee & Commission Policy</a></li>
                </ul>
            </div>
            <div class="footer-col">
                <h5>Customer Support</h5>
                <ul class="footer-links">
                    <li><a href="${pageContext.request.contextPath}/orders">Track Your Order</a></li>
                    <li><a href="${pageContext.request.contextPath}/profile">Account Settings</a></li>
                    <li><a href="${pageContext.request.contextPath}/addresses">Manage Addresses</a></li>
                    <li><a href="${pageContext.request.contextPath}/wishlist">My Wishlist</a></li>
                </ul>
            </div>
        </div>

        <div class="footer-bottom">
            <p>&copy; 2026 HariMart India Marketplace Ltd. All rights reserved. Built with Java Servlets, JSP & H2 Database.</p>
        </div>
    </div>
</footer>

<!-- Mobile Bottom Navigation Bar -->
<div class="mobile-bottom-nav">
    <a href="${pageContext.request.contextPath}/" class="mobile-nav-item ${empty param.slug ? 'active' : ''}">
        <span class="mobile-nav-icon">🏠</span>
        <span>Home</span>
    </a>
    <a href="${pageContext.request.contextPath}/search" class="mobile-nav-item">
        <span class="mobile-nav-icon">🔍</span>
        <span>Search</span>
    </a>
    <a href="${pageContext.request.contextPath}/wishlist" class="mobile-nav-item">
        <span class="mobile-nav-icon">❤️</span>
        <span>Wishlist</span>
    </a>
    <a href="${pageContext.request.contextPath}/cart" class="mobile-nav-item">
        <span class="mobile-nav-icon">🛒</span>
        <span>Cart</span>
    </a>
    <a href="${pageContext.request.contextPath}/profile" class="mobile-nav-item">
        <span class="mobile-nav-icon">👤</span>
        <span>Account</span>
    </a>
</div>

<!-- Toast Container -->
<div id="toastContainer" class="toast-container"></div>

<!-- Core Scripts -->
<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/cart.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/search.js"></script>
