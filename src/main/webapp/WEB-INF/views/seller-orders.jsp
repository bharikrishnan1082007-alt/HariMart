<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Customer Orders — Seller Central — HariMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/responsive.css">
</head>
<body>

    <jsp:include page="includes/header.jsp"/>

    <div class="dashboard-container">
        <div class="dashboard-header">
            <div class="dashboard-title">
                <h2>Fulfillment & Customer Orders</h2>
                <p>Track purchased items and update tracking dispatch stages in real time.</p>
            </div>
            <a href="${pageContext.request.contextPath}/seller/dashboard" class="btn-action-sm" style="padding: 0.65rem 1.1rem;">
                ← Back to Dashboard
            </a>
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
                <h4>All Order Items (${orders.size()})</h4>
            </div>
            <div class="data-table-wrapper">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Order #</th>
                            <th>Product</th>
                            <th>Qty</th>
                            <th>Total</th>
                            <th>Current Status</th>
                            <th>Update Fulfillment</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty orders}">
                                <tr>
                                    <td colspan="6" style="text-align: center; padding: 3rem; color: var(--slate-400);">
                                        <p style="font-size: 1.1rem;">No orders placed for your store yet.</p>
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="item" items="${orders}">
                                    <tr>
                                        <td>
                                            <span style="font-weight: 700; color: var(--navy-800); font-family: monospace;">
                                                ${item.orderNumber != null ? item.orderNumber : ('ORD-'.concat(item.orderId))}
                                            </span>
                                        </td>
                                        <td>
                                            <div style="display: flex; align-items: center; gap: 0.75rem;">
                                                <img src="${item.productImageUrl}" alt="${item.productName}" style="width: 40px; height: 40px; object-fit: contain; border-radius: 4px; border: 1px solid var(--slate-200); background-color: #ffffff;">
                                                <div style="font-weight: 600; color: var(--navy-900); max-width: 280px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
                                                    ${item.productName}
                                                </div>
                                            </div>
                                        </td>
                                        <td>${item.quantity}</td>
                                        <td style="font-weight: 700; color: var(--navy-900);">${item.formattedTotalPrice}</td>
                                        <td>
                                            <span class="status-pill ${item.fulfillmentStatus.toLowerCase()}">
                                                ${item.fulfillmentStatus}
                                            </span>
                                        </td>
                                        <td>
                                            <form action="${pageContext.request.contextPath}/seller/orders/status" method="POST" style="display: flex; align-items: center; gap: 0.5rem;">
                                                <input type="hidden" name="orderItemId" value="${item.id}">
                                                <select name="status" class="form-control" style="padding: 0.35rem 0.6rem; font-size: 0.85rem; width: auto;">
                                                    <option value="CONFIRMED" ${item.fulfillmentStatus == 'CONFIRMED' ? 'selected' : ''}>CONFIRMED</option>
                                                    <option value="PACKED" ${item.fulfillmentStatus == 'PACKED' ? 'selected' : ''}>PACKED</option>
                                                    <option value="SHIPPED" ${item.fulfillmentStatus == 'SHIPPED' ? 'selected' : ''}>SHIPPED</option>
                                                    <option value="DELIVERED" ${item.fulfillmentStatus == 'DELIVERED' ? 'selected' : ''}>DELIVERED</option>
                                                    <option value="CANCELLED" ${item.fulfillmentStatus == 'CANCELLED' ? 'selected' : ''}>CANCELLED</option>
                                                </select>
                                                <button type="submit" class="btn-action-sm" style="font-size: 0.8rem;">
                                                    Update
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
