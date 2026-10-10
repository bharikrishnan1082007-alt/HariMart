<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Catalog Moderation — Admin Console — HariMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/responsive.css">
</head>
<body>

    <jsp:include page="includes/header.jsp"/>

    <div class="dashboard-container">
        <div class="dashboard-header">
            <div class="dashboard-title">
                <h2>Product Catalog & Content Moderation</h2>
                <p>Review products listed by marketplace vendors, enforce quality standards, or suspend non-compliant listings.</p>
            </div>
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn-action-sm" style="padding: 0.65rem 1.1rem;">
                ← Back to Admin Console
            </a>
        </div>

        <c:if test="${not empty param.msg}">
            <div style="background-color: var(--emerald-100); color: var(--emerald-600); padding: 0.85rem 1.25rem; border-radius: 8px; margin-bottom: 1.5rem; font-weight: 600;">
                ✓ ${param.msg}
            </div>
        </c:if>

        <div class="data-card">
            <div class="data-card-header">
                <h4>All Listed Products (${products.size()})</h4>
            </div>
            <div class="data-table-wrapper">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Product Info</th>
                            <th>Merchant Store</th>
                            <th>Category</th>
                            <th>Price</th>
                            <th>Stock</th>
                            <th>Status</th>
                            <th style="text-align: right;">Moderation Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty products}">
                                <tr>
                                    <td colspan="7" style="text-align: center; padding: 3rem; color: var(--slate-400);">
                                        No products in marketplace database.
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
                                                    <div style="font-size: 0.8rem; color: var(--slate-400);">Brand: ${p.brand} • SKU #${p.id}</div>
                                                </div>
                                            </div>
                                        </td>
                                        <td>
                                            <span style="font-weight: 600; color: var(--navy-900);">
                                                ${p.sellerStoreName != null ? p.sellerStoreName : ('Seller #'.concat(p.sellerId))}
                                            </span>
                                        </td>
                                        <td>${p.categoryName != null ? p.categoryName : 'General'}</td>
                                        <td>
                                            <strong style="color: var(--navy-900);">${p.formattedPrice}</strong>
                                        </td>
                                        <td>
                                            ${p.stockQuantity} units
                                        </td>
                                        <td>
                                            <span class="status-pill ${p.isActive ? 'active' : 'suspended'}">
                                                ${p.isActive ? 'Active' : 'Hidden'}
                                            </span>
                                        </td>
                                        <td style="text-align: right;">
                                            <form action="${pageContext.request.contextPath}/admin/products/toggle" method="POST" style="display: inline;">
                                                <input type="hidden" name="productId" value="${p.id}">
                                                <button type="submit" class="btn-action-sm" style="${p.isActive ? 'color: var(--rose-600);' : 'color: var(--emerald-600);'}">
                                                    ${p.isActive ? 'Suspend / Hide' : 'Approve / Show'}
                                                </button>
                                            </form>
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
