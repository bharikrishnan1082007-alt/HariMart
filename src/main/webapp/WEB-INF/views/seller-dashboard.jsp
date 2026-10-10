<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Seller Dashboard — HariMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/responsive.css">
</head>
<body>

    <jsp:include page="includes/header.jsp"/>

    <div class="dashboard-container">
        <!-- Dashboard Header -->
        <div class="dashboard-header">
            <div class="dashboard-title">
                <h2>Merchant Central: ${seller.storeName}</h2>
                <p>Status: <span class="status-pill approved">${seller.status}</span> • Commission: <strong>${seller.commissionRate}%</strong></p>
            </div>
            <div style="display: flex; gap: 0.75rem;">
                <a href="${pageContext.request.contextPath}/seller/products/add" class="btn-primary" style="padding: 0.65rem 1.25rem; font-size: 0.9rem;">
                    + Add Product
                </a>
                <a href="${pageContext.request.contextPath}/seller/orders" class="btn-action-sm" style="padding: 0.65rem 1rem;">
                    📦 Orders (${analytics.totalOrders})
                </a>
                <a href="${pageContext.request.contextPath}/seller/analytics" class="btn-action-sm" style="padding: 0.65rem 1rem;">
                    📊 Analytics
                </a>
            </div>
        </div>

        <c:if test="${not empty param.msg}">
            <div style="background-color: var(--emerald-100); color: var(--emerald-600); padding: 0.85rem 1.25rem; border-radius: 8px; margin-bottom: 2rem; font-weight: 600;">
                ✓ ${param.msg}
            </div>
        </c:if>

        <!-- KPI Metrics Grid -->
        <div class="metrics-grid">
            <div class="metric-card">
                <div class="metric-icon-box amber">💰</div>
                <div class="metric-info">
                    <h6>Gross Sales</h6>
                    <h3>${analytics.formattedRevenue}</h3>
                </div>
            </div>
            <div class="metric-card">
                <div class="metric-icon-box emerald">💵</div>
                <div class="metric-info">
                    <h6>Net Payout</h6>
                    <h3>${analytics.formattedSellerEarnings}</h3>
                </div>
            </div>
            <div class="metric-card">
                <div class="metric-icon-box blue">📦</div>
                <div class="metric-info">
                    <h6>Total Orders</h6>
                    <h3>${analytics.totalOrders}</h3>
                </div>
            </div>
            <div class="metric-card">
                <div class="metric-icon-box rose">🛍️</div>
                <div class="metric-info">
                    <h6>Listed Items</h6>
                    <h3>${analytics.totalProducts}</h3>
                </div>
            </div>
        </div>

        <!-- Recent Order Items Table -->
        <div class="data-card">
            <div class="data-card-header">
                <h4>Recent Store Orders</h4>
                <a href="${pageContext.request.contextPath}/seller/orders" style="font-size: 0.85rem; color: var(--navy-600); font-weight: 700;">View All Orders →</a>
            </div>
            <div class="data-table-wrapper">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Order #</th>
                            <th>Product</th>
                            <th>Qty</th>
                            <th>Total Price</th>
                            <th>Fulfillment Status</th>
                            <th>Date</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty recentOrders}">
                                <c:forEach var="ord" items="${recentOrders}">
                                    <tr>
                                        <td><strong>${ord.orderNumber}</strong></td>
                                        <td>${ord.productName}</td>
                                        <td>${ord.quantity}</td>
                                        <td><strong>${ord.formattedTotalPrice}</strong></td>
                                        <td>
                                            <span class="status-pill ${ord.fulfillmentStatus.toLowerCase()}">${ord.fulfillmentStatus}</span>
                                        </td>
                                        <td>${ord.createdAt}</td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="6" style="text-align: center; color: var(--slate-400); padding: 2rem;">No orders received yet for this store.</td>
                                </tr>
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
