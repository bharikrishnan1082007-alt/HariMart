<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Operations Console — HariMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/responsive.css">
</head>
<body>

    <jsp:include page="includes/header.jsp"/>

    <div class="dashboard-container">
        <!-- Admin Navigation Header -->
        <div class="dashboard-header">
            <div class="dashboard-title">
                <h2>HariMart Admin Operations Console</h2>
                <p>Platform governance, multi-vendor commission accounting, and catalog moderation.</p>
            </div>
            <div style="display: flex; gap: 0.5rem; flex-wrap: wrap;">
                <a href="${pageContext.request.contextPath}/admin/users" class="btn-action-sm" style="padding: 0.6rem 1rem;">
                    👥 Users (${analytics.totalUsers})
                </a>
                <a href="${pageContext.request.contextPath}/admin/sellers" class="btn-action-sm" style="padding: 0.6rem 1rem;">
                    🏪 Sellers (${analytics.totalSellers})
                </a>
                <a href="${pageContext.request.contextPath}/admin/products" class="btn-action-sm" style="padding: 0.6rem 1rem;">
                    📦 Products (${analytics.totalProducts})
                </a>
                <a href="${pageContext.request.contextPath}/admin/categories" class="btn-action-sm" style="padding: 0.6rem 1rem;">
                    🏷 Categories
                </a>
            </div>
        </div>

        <c:if test="${not empty param.msg}">
            <div style="background-color: var(--emerald-100); color: var(--emerald-600); padding: 0.85rem 1.25rem; border-radius: 8px; margin-bottom: 2rem; font-weight: 600;">
                ✓ ${param.msg}
            </div>
        </c:if>
        <c:if test="${not empty param.err}">
            <div style="background-color: var(--rose-100); color: var(--rose-600); padding: 0.85rem 1.25rem; border-radius: 8px; margin-bottom: 2rem; font-weight: 600;">
                ⚠ ${param.err}
            </div>
        </c:if>

        <!-- KPI Metrics Grid -->
        <div class="metrics-grid">
            <div class="metric-card">
                <div class="metric-icon-box amber">📈</div>
                <div class="metric-info">
                    <h6>Platform GMV</h6>
                    <h3>${analytics.formattedRevenue}</h3>
                </div>
            </div>
            <div class="metric-card">
                <div class="metric-icon-box emerald">💰</div>
                <div class="metric-info">
                    <h6>Commission Revenue</h6>
                    <h3>${analytics.formattedCommission}</h3>
                </div>
            </div>
            <div class="metric-card">
                <div class="metric-icon-box blue">🛍</div>
                <div class="metric-info">
                    <h6>Total Platform Orders</h6>
                    <h3>${analytics.totalOrders}</h3>
                </div>
            </div>
            <div class="metric-card">
                <div class="metric-icon-box rose">👥</div>
                <div class="metric-info">
                    <h6>Registered Buyers</h6>
                    <h3>${analytics.totalUsers}</h3>
                </div>
            </div>
        </div>

        <!-- Recent Orders Table -->
        <div class="data-card">
            <div class="data-card-header">
                <h4>Recent Platform Orders</h4>
                <span style="font-size: 0.85rem; color: var(--slate-500);">Live stream of recent purchases</span>
            </div>
            <div class="data-table-wrapper">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Order ID</th>
                            <th>Customer</th>
                            <th>Date</th>
                            <th>Payment</th>
                            <th>Total Amount</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty recentOrders}">
                                <tr>
                                    <td colspan="7" style="text-align: center; padding: 2.5rem; color: var(--slate-400);">
                                        No transactions recorded yet.
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="order" items="${recentOrders}">
                                    <tr>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/orders/detail?id=${order.id}" style="font-weight: 700; color: var(--navy-800); text-decoration: none; font-family: monospace;">
                                                ${order.orderNumber}
                                            </a>
                                        </td>
                                        <td>
                                            ${order.buyer != null ? order.buyer.name : ('User #'.concat(order.userId))}
                                        </td>
                                        <td style="color: var(--slate-500); font-size: 0.85rem;">
                                            ${order.createdAt}
                                        </td>
                                        <td>
                                            <span style="font-weight: 600; font-size: 0.85rem;">${order.paymentMethod}</span>
                                            <span class="status-pill ${order.paymentStatus.toLowerCase()}" style="font-size: 0.7rem; margin-left: 0.3rem;">
                                                ${order.paymentStatus}
                                            </span>
                                        </td>
                                        <td style="font-weight: 700; color: var(--navy-900);">
                                            ${order.formattedTotal}
                                        </td>
                                        <td>
                                            <span class="status-pill ${order.orderStatus.toLowerCase()}">
                                                ${order.orderStatus}
                                            </span>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/orders/detail?id=${order.id}" class="btn-action-sm">
                                                View Details
                                            </a>
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
