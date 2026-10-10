<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${category.name} — HariMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/responsive.css">
    <style>
        .catalog-layout {
            display: grid;
            grid-template-columns: 260px 1fr;
            gap: 2.5rem;
            margin-top: 2rem;
            margin-bottom: 4rem;
        }
        .filter-sidebar {
            background-color: #ffffff;
            border-radius: var(--radius-lg);
            padding: 1.5rem;
            border: 1px solid var(--slate-200);
            height: fit-content;
        }
        .filter-group {
            margin-bottom: 1.5rem;
            padding-bottom: 1.5rem;
            border-bottom: 1px solid var(--slate-100);
        }
        .filter-title {
            font-size: 0.95rem;
            font-weight: 700;
            color: var(--navy-800);
            margin-bottom: 0.75rem;
        }
        @media(max-width: 900px) {
            .catalog-layout { grid-template-columns: 1fr; }
        }
    </style>
</head>
<body>

    <jsp:include page="includes/header.jsp"/>

    <main class="container" style="padding-top: 1.5rem;">

        <!-- Breadcrumbs -->
        <div style="font-size: 0.85rem; color: var(--slate-500); margin-bottom: 1.5rem;">
            <a href="${pageContext.request.contextPath}/">Home</a> &gt; 
            <span>Categories</span> &gt; 
            <span style="color: var(--slate-800); font-weight: 600;">${category.name}</span>
        </div>

        <div style="background: linear-gradient(135deg, var(--navy-800), var(--navy-900)); color: #fff; padding: 2rem; border-radius: var(--radius-xl); margin-bottom: 2rem;">
            <h1 style="font-size: 2rem; font-weight: 800; margin-bottom: 0.35rem;">${category.name}</h1>
            <p style="color: var(--slate-300); font-size: 1rem; max-width: 600px;">${category.description}</p>
        </div>

        <div class="catalog-layout">
            <!-- Filter Sidebar -->
            <aside class="filter-sidebar">
                <form action="${pageContext.request.contextPath}/category" method="GET">
                    <input type="hidden" name="slug" value="${category.slug}">

                    <div class="filter-group">
                        <div class="filter-title">Sort By</div>
                        <select name="sort" class="form-control" onchange="this.form.submit()">
                            <option value="newest" ${currentSort == 'newest' ? 'selected' : ''}>Newest Arrivals</option>
                            <option value="popular" ${currentSort == 'popular' ? 'selected' : ''}>Most Popular</option>
                            <option value="price_asc" ${currentSort == 'price_asc' ? 'selected' : ''}>Price: Low to High</option>
                            <option value="price_desc" ${currentSort == 'price_desc' ? 'selected' : ''}>Price: High to Low</option>
                            <option value="rating" ${currentSort == 'rating' ? 'selected' : ''}>Highest Customer Rating</option>
                        </select>
                    </div>

                    <div class="filter-group">
                        <div class="filter-title">Price Range (₹)</div>
                        <div style="display: flex; gap: 0.5rem; align-items: center; margin-bottom: 0.75rem;">
                            <input type="number" name="minPrice" placeholder="Min" value="${param.minPrice}" class="form-control" style="padding: 0.45rem;">
                            <span>-</span>
                            <input type="number" name="maxPrice" placeholder="Max" value="${param.maxPrice}" class="form-control" style="padding: 0.45rem;">
                        </div>
                    </div>

                    <div class="filter-group">
                        <div class="filter-title">Customer Rating</div>
                        <label style="display: flex; align-items: center; gap: 0.5rem; font-size: 0.9rem; margin-bottom: 0.35rem;">
                            <input type="radio" name="rating" value="4" ${param.rating == '4' ? 'checked' : ''}>
                            <span style="color: var(--amber-500);">★★★★☆</span> 4★ & above
                        </label>
                        <label style="display: flex; align-items: center; gap: 0.5rem; font-size: 0.9rem;">
                            <input type="radio" name="rating" value="3" ${param.rating == '3' ? 'checked' : ''}>
                            <span style="color: var(--amber-500);">★★★☆☆</span> 3★ & above
                        </label>
                    </div>

                    <div class="filter-group">
                        <div class="filter-title">Availability</div>
                        <label style="display: flex; align-items: center; gap: 0.5rem; font-size: 0.9rem;">
                            <input type="checkbox" name="inStock" value="true" ${param.inStock == 'true' ? 'checked' : ''}>
                            <span>Include Out of Stock</span>
                        </label>
                    </div>

                    <button type="submit" class="btn-primary" style="width: 100%; justify-content: center; font-size: 0.9rem; padding: 0.65rem;">
                        Apply Filters
                    </button>
                    <a href="${pageContext.request.contextPath}/category?slug=${category.slug}" style="display: block; text-align: center; font-size: 0.85rem; color: var(--slate-500); margin-top: 0.75rem;">Reset Filters</a>
                </form>
            </aside>

            <!-- Product Grid -->
            <div>
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
                    <div style="font-size: 0.95rem; color: var(--slate-600);">
                        Showing <strong>${products.size()}</strong> of <strong>${totalCount}</strong> items
                    </div>
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
                    </c:when>
                    <c:otherwise>
                        <div style="background-color: #ffffff; padding: 3rem; text-align: center; border-radius: var(--radius-lg); border: 1px solid var(--slate-200);">
                            <div style="font-size: 2.5rem; margin-bottom: 0.75rem;">🔍</div>
                            <h3 style="font-size: 1.25rem; font-weight: 700; color: var(--navy-800);">No products found matching your filters</h3>
                            <p style="color: var(--slate-500); margin-top: 0.5rem;">Try adjusting your price range or sorting criteria.</p>
                            <a href="${pageContext.request.contextPath}/category?slug=${category.slug}" class="btn-primary" style="margin-top: 1.5rem; display: inline-flex;">Clear Filters</a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

    </main>

    <jsp:include page="includes/footer.jsp"/>

</body>
</html>
