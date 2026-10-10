<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Wishlist — HariMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/responsive.css">
</head>
<body>

    <jsp:include page="includes/header.jsp"/>

    <main class="container" style="padding-top: 2.5rem; padding-bottom: 5rem;">

        <!-- Breadcrumbs -->
        <div style="font-size: 0.85rem; color: var(--slate-500); margin-bottom: 1.5rem;">
            <a href="${pageContext.request.contextPath}/">Home</a> &gt; 
            <a href="${pageContext.request.contextPath}/profile">Account</a> &gt; 
            <span style="color: var(--slate-800); font-weight: 600;">Wishlist</span>
        </div>

        <div style="margin-bottom: 2rem;">
            <h1 style="font-size: 1.85rem; font-weight: 800; color: var(--navy-800);">Your Saved Wishlist</h1>
            <p style="color: var(--slate-500); font-size: 0.95rem;">Keep track of items you love and move them directly to your cart</p>
        </div>

        <c:choose>
            <c:when test="${not empty wishlist}">
                <div class="products-grid">
                    <c:forEach var="w" items="${wishlist}">
                        <div class="product-card" id="wishlistCard-${w.productId}">
                            <div class="product-thumb-container">
                                <a href="${pageContext.request.contextPath}/product?id=${w.product.id}" style="width: 100%; height: 100%;">
                                    <img src="${w.product.imageUrl}" alt="${w.product.name}" class="product-thumb">
                                </a>
                            </div>
                            <div class="product-content">
                                <div class="product-brand">${w.product.brand}</div>
                                <a href="${pageContext.request.contextPath}/product?id=${w.product.id}" class="product-title">${w.product.name}</a>
                                <div class="product-price-row">
                                    <span class="price-current">${w.product.formattedPrice}</span>
                                </div>
                                <div class="product-card-footer" style="flex-direction: column;">
                                    <button type="button" class="btn-add-cart" onclick="addToCartAjax(${w.product.id}, 1, '${pageContext.request.contextPath}')">
                                        🛒 Move to Cart
                                    </button>
                                    <form action="${pageContext.request.contextPath}/wishlist/remove" method="POST" style="margin: 0; width: 100%;">
                                        <input type="hidden" name="productId" value="${w.productId}">
                                        <button type="submit" class="btn-action-sm" style="width: 100%; justify-content: center; color: var(--rose-600); margin-top: 0.35rem;">
                                            ✕ Remove
                                        </button>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <div style="background-color: #ffffff; padding: 4rem 2rem; text-align: center; border-radius: var(--radius-xl); border: 1px solid var(--slate-200);">
                    <div style="font-size: 3rem; margin-bottom: 1rem;">❤️</div>
                    <h2 style="font-size: 1.5rem; font-weight: 800; color: var(--navy-800);">Your Wishlist is Empty</h2>
                    <p style="color: var(--slate-500); margin-top: 0.5rem;">Explore our curated catalog and tap the heart icon to save products.</p>
                    <a href="${pageContext.request.contextPath}/" class="btn-primary" style="margin-top: 1.5rem; display: inline-flex;">Explore Today's Deals</a>
                </div>
            </c:otherwise>
        </c:choose>

    </main>

    <jsp:include page="includes/footer.jsp"/>

</body>
</html>
