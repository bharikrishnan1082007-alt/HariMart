<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Search Results for "${keyword}" — HariMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/responsive.css">
</head>
<body>

    <jsp:include page="includes/header.jsp"/>

    <main class="container" style="padding-top: 1.5rem; margin-bottom: 4rem;">

        <!-- Breadcrumbs -->
        <div style="font-size: 0.85rem; color: var(--slate-500); margin-bottom: 1.5rem;">
            <a href="${pageContext.request.contextPath}/">Home</a> &gt; 
            <span>Search</span> &gt; 
            <span style="color: var(--slate-800); font-weight: 600;">"${keyword}"</span>
        </div>

        <div style="margin-bottom: 2rem;">
            <h1 style="font-size: 1.75rem; font-weight: 800; color: var(--navy-800);">
                Search Results for <span style="color: var(--navy-600);">"${keyword}"</span>
            </h1>
            <p style="color: var(--slate-500); font-size: 0.95rem;">
                Found <strong>${totalCount}</strong> matching items
            </p>
        </div>

        <c:choose>
            <c:when test="${not empty products}">
                <div class="products-grid">
                    <c:forEach var="p" items="${products}">
                        <div class="product-card">
                            <div class="product-thumb-container">
                                <c:if test="${p.hasDiscount()}">
                                    <span class="badge-discount">${p.discountPercentage}% OFF</span>
                                </c:if>
                                <button class="wishlist-btn wishlist-toggle-btn" data-product-id="${p.id}" data-context-path="${pageContext.request.contextPath}">♥</button>
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
            </c:when>
            <c:otherwise>
                <div style="background-color: #ffffff; padding: 3.5rem; text-align: center; border-radius: var(--radius-lg); border: 1px solid var(--slate-200);">
                    <div style="font-size: 3rem; margin-bottom: 0.75rem;">🔍</div>
                    <h3 style="font-size: 1.35rem; font-weight: 700; color: var(--navy-800);">No results found for "${keyword}"</h3>
                    <p style="color: var(--slate-500); margin-top: 0.5rem; max-width: 500px; margin-left: auto; margin-right: auto;">
                        Please check your spelling, try different keywords, or browse through our category catalog.
                    </p>
                    <a href="${pageContext.request.contextPath}/" class="btn-primary" style="margin-top: 1.5rem; display: inline-flex;">Return to Storefront</a>
                </div>
            </c:otherwise>
        </c:choose>

    </main>

    <jsp:include page="includes/footer.jsp"/>

</body>
</html>
