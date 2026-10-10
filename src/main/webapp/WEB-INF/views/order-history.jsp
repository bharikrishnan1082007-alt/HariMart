<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Your Orders — HariMart</title>
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
            <span style="color: var(--slate-800); font-weight: 600;">Orders</span>
        </div>

        <div style="display: flex; align-items: baseline; justify-content: space-between; margin-bottom: 2rem;">
            <div>
                <h1 style="font-size: 1.85rem; font-weight: 800; color: var(--navy-800);">Your Past Orders</h1>
                <p style="color: var(--slate-500); font-size: 0.95rem;">Review past purchases, track active shipments, or download receipts</p>
            </div>
        </div>

        <c:choose>
            <c:when test="${not empty orders}">
                <div style="display: flex; flex-direction: column; gap: 1.75rem;">
                    <c:forEach var="ord" items="${orders}">
                        <div style="background-color: #ffffff; border-radius: var(--radius-xl); border: 1px solid var(--slate-200); box-shadow: var(--shadow-sm); overflow: hidden;">
                            <!-- Order Header -->
                            <div style="background-color: var(--ivory-50); border-bottom: 1px solid var(--slate-200); padding: 1rem 1.5rem; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem; font-size: 0.88rem;">
                                <div style="display: flex; gap: 2.5rem; flex-wrap: wrap;">
                                    <div>
                                        <div style="color: var(--slate-400); text-transform: uppercase; font-size: 0.75rem; font-weight: 700;">Order Placed</div>
                                        <div style="font-weight: 700; color: var(--slate-800);">${ord.createdAt}</div>
                                    </div>
                                    <div>
                                        <div style="color: var(--slate-400); text-transform: uppercase; font-size: 0.75rem; font-weight: 700;">Total</div>
                                        <div style="font-weight: 800; color: var(--navy-800);">${ord.formattedTotal}</div>
                                    </div>
                                    <div>
                                        <div style="color: var(--slate-400); text-transform: uppercase; font-size: 0.75rem; font-weight: 700;">Ship To</div>
                                        <div style="font-weight: 600; color: var(--slate-800);">${ord.shippingAddress != null ? ord.shippingAddress.fullName : 'Saved Address'}</div>
                                    </div>
                                </div>
                                <div style="display: flex; gap: 1rem; align-items: center;">
                                    <div style="text-align: right;">
                                        <div style="color: var(--slate-400); text-transform: uppercase; font-size: 0.75rem; font-weight: 700;">Order #</div>
                                        <div style="font-weight: 700; color: var(--navy-600);">${ord.orderNumber}</div>
                                    </div>
                                    <a href="${pageContext.request.contextPath}/orders/detail?id=${ord.id}" class="btn-action-sm">
                                        View Details
                                    </a>
                                </div>
                            </div>

                            <!-- Order Items -->
                            <div style="padding: 1.5rem;">
                                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
                                    <span style="font-weight: 700; font-size: 1.05rem; color: var(--navy-800);">
                                        Status: <span style="color: ${ord.orderStatus == 'DELIVERED' ? 'var(--emerald-600)' : (ord.orderStatus == 'CANCELLED' ? 'var(--rose-600)' : 'var(--amber-500)')};">${ord.orderStatus}</span>
                                    </span>
                                </div>

                                <div style="display: flex; flex-direction: column; gap: 1.25rem;">
                                    <c:forEach var="item" items="${ord.items}">
                                        <div style="display: flex; gap: 1.25rem; align-items: center;">
                                            <img src="${item.productImageUrl}" style="width: 70px; height: 70px; object-fit: cover; border-radius: 8px;">
                                            <div style="flex: 1;">
                                                <div style="font-weight: 700; font-size: 1rem; color: var(--navy-800);">${item.productName}</div>
                                                <div style="font-size: 0.85rem; color: var(--slate-500); margin-top: 0.2rem;">
                                                    Qty: ${item.quantity} • Unit Price: ${item.formattedUnitPrice} • Sold by: ${item.sellerStoreName}
                                                </div>
                                            </div>
                                            <div style="font-weight: 800; color: var(--navy-800);">${item.formattedTotalPrice}</div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <div style="background-color: #ffffff; padding: 4rem 2rem; text-align: center; border-radius: var(--radius-xl); border: 1px solid var(--slate-200);">
                    <div style="font-size: 3rem; margin-bottom: 1rem;">📦</div>
                    <h2 style="font-size: 1.5rem; font-weight: 800; color: var(--navy-800);">No orders placed yet</h2>
                    <p style="color: var(--slate-500); margin-top: 0.5rem;">Your order history will appear here once you make your first purchase.</p>
                    <a href="${pageContext.request.contextPath}/" class="btn-primary" style="margin-top: 1.5rem; display: inline-flex;">Start Shopping</a>
                </div>
            </c:otherwise>
        </c:choose>

    </main>

    <jsp:include page="includes/footer.jsp"/>

</body>
</html>
