<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Products — Seller Central — HariMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/responsive.css">
</head>
<body>

    <jsp:include page="includes/header.jsp"/>

    <div class="dashboard-container">
        <div class="dashboard-header">
            <div class="dashboard-title">
                <h2>Product Catalog & Inventory</h2>
                <p>Manage your listings, stock availability, and retail pricing.</p>
            </div>
            <div style="display: flex; gap: 0.75rem;">
                <a href="${pageContext.request.contextPath}/seller/dashboard" class="btn-action-sm" style="padding: 0.65rem 1.1rem;">
                    ← Back to Dashboard
                </a>
                <a href="${pageContext.request.contextPath}/seller/products/add" class="btn-primary" style="padding: 0.65rem 1.25rem; font-size: 0.9rem;">
                    + Add New Product
                </a>
            </div>
        </div>

        <c:if test="${not empty param.msg}">
            <div style="background-color: var(--emerald-100); color: var(--emerald-600); padding: 0.85rem 1.25rem; border-radius: 8px; margin-bottom: 1.5rem; font-weight: 600;">
                ✓ ${param.msg}
            </div>
        </c:if>
        <c:if test="${not empty param.err}">
            <div style="background-color: var(--rose-100); color: var(--rose-600); padding: 0.85rem 1.25rem; border-radius: 8px; margin-bottom: 1.5rem; font-weight: 600;">
                ⚠ ${param.err}
            </div>
        </c:if>

        <div class="data-card">
            <div class="data-card-header">
                <h4>All Listings (${products.size()})</h4>
                <a href="${pageContext.request.contextPath}/seller/products/add" class="btn-action-sm" style="font-size: 0.85rem;">
                    + Add Product
                </a>
            </div>
            <div class="data-table-wrapper">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Item</th>
                            <th>Brand</th>
                            <th>Price</th>
                            <th>Stock</th>
                            <th>Status</th>
                            <th style="text-align: right;">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty products}">
                                <tr>
                                    <td colspan="6" style="text-align: center; padding: 3rem; color: var(--slate-400);">
                                        <p style="font-size: 1.1rem; margin-bottom: 0.75rem;">No products listed yet.</p>
                                        <a href="${pageContext.request.contextPath}/seller/products/add" class="btn-primary" style="padding: 0.5rem 1rem; font-size: 0.88rem;">Add your first product</a>
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="p" items="${products}">
                                    <tr>
                                        <td>
                                            <div style="display: flex; align-items: center; gap: 0.85rem;">
                                                <img src="${p.imageUrl}" alt="${p.name}" style="width: 44px; height: 44px; object-fit: contain; border-radius: 6px; background-color: #f8fafc; border: 1px solid var(--slate-200);">
                                                <div>
                                                    <a href="${pageContext.request.contextPath}/product?id=${p.id}" target="_blank" style="font-weight: 600; color: var(--navy-800); text-decoration: none;">
                                                        ${p.name}
                                                    </a>
                                                    <div style="font-size: 0.8rem; color: var(--slate-400);">SKU #${p.id}</div>
                                                </div>
                                            </div>
                                        </td>
                                        <td>${p.brand}</td>
                                        <td>
                                            <div style="font-weight: 700; color: var(--navy-900);">${p.formattedPrice}</div>
                                            <c:if test="${p.hasDiscount()}">
                                                <div style="font-size: 0.78rem; color: var(--slate-400); text-decoration: line-through;">${p.formattedOriginalPrice}</div>
                                            </c:if>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${p.stockQuantity > 10}">
                                                    <span style="font-weight: 600; color: var(--emerald-600);">${p.stockQuantity} in stock</span>
                                                </c:when>
                                                <c:when test="${p.stockQuantity > 0}">
                                                    <span style="font-weight: 600; color: var(--amber-600);">Only ${p.stockQuantity} left</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span style="font-weight: 600; color: var(--rose-600);">Out of Stock</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <span class="status-pill ${p.isActive ? 'active' : 'inactive'}">
                                                ${p.isActive ? 'Active' : 'Draft / Off'}
                                            </span>
                                        </td>
                                        <td style="text-align: right;">
                                            <div style="display: inline-flex; gap: 0.4rem;">
                                                <a href="${pageContext.request.contextPath}/seller/products/edit?id=${p.id}" class="btn-action-sm">
                                                    ✏ Edit
                                                </a>
                                                <form action="${pageContext.request.contextPath}/seller/products/toggle" method="POST" style="display: inline;">
                                                    <input type="hidden" name="productId" value="${p.id}">
                                                    <button type="submit" class="btn-action-sm" title="Toggle Stock status">
                                                        ${p.inStock ? 'Mark Out' : 'Restock'}
                                                    </button>
                                                </form>
                                                <form action="${pageContext.request.contextPath}/seller/products/delete" method="POST" style="display: inline;" onsubmit="return confirm('Are you sure you want to delete this listing?');">
                                                    <input type="hidden" name="productId" value="${p.id}">
                                                    <button type="submit" class="btn-action-sm" style="color: var(--rose-600); border-color: var(--rose-200);">
                                                        🗑
                                                    </button>
                                                </form>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <jsp:include page="includes/footer.jsp"/>

</body>
</html>
