<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Order #${order.orderNumber} — HariMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/responsive.css">
    <style>
        .stepper-container {
            display: flex;
            justify-content: space-between;
            position: relative;
            margin: 2.5rem 0 3.5rem;
        }
        .stepper-progress-bar {
            position: absolute;
            top: 20px;
            left: 0;
            right: 0;
            height: 4px;
            background-color: var(--slate-200);
            z-index: 1;
        }
        .stepper-progress-fill {
            height: 100%;
            background-color: var(--emerald-600);
            transition: width 0.4s ease;
        }
        .step-node {
            position: relative;
            z-index: 2;
            background-color: #ffffff;
            display: flex;
            flex-direction: column;
            align-items: center;
            text-align: center;
        }
        .step-circle {
            width: 42px;
            height: 42px;
            border-radius: var(--radius-full);
            background-color: #ffffff;
            border: 3px solid var(--slate-300);
            color: var(--slate-400);
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 800;
            font-size: 1rem;
            margin-bottom: 0.5rem;
        }
        .step-circle.active {
            border-color: var(--emerald-600);
            background-color: var(--emerald-600);
            color: #ffffff;
        }
        .step-label {
            font-size: 0.85rem;
            font-weight: 700;
            color: var(--slate-600);
        }
        @media print {
            .site-header, .site-footer, .no-print, .stepper-container { display: none !important; }
            body { background: #fff !important; }
        }
    </style>
</head>
<body>

    <jsp:include page="includes/header.jsp"/>

    <main class="container" style="padding-top: 2rem; padding-bottom: 5rem;">

        <!-- Header Actions -->
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem; flex-wrap: wrap; gap: 1rem;">
            <div>
                <h1 style="font-size: 1.85rem; font-weight: 800; color: var(--navy-800);">
                    Order Details
                </h1>
                <p style="color: var(--slate-500); font-size: 0.95rem;">
                    Order Number: <strong>${order.orderNumber}</strong> • Placed on ${order.createdAt}
                </p>
            </div>
            <div class="no-print" style="display: flex; gap: 0.75rem;">
                <button type="button" class="btn-action-sm" onclick="window.print()">
                    🖨️ Print Tax Invoice
                </button>
                <c:if test="${order.canCancel()}">
                    <form action="${pageContext.request.contextPath}/orders/cancel" method="POST" onsubmit="return confirm('Are you sure you want to cancel this order? Stock will be immediately restored.');">
                        <input type="hidden" name="orderId" value="${order.id}">
                        <button type="submit" class="btn-action-sm" style="color: var(--rose-600); border-color: var(--rose-600);">
                            ✕ Cancel Order
                        </button>
                    </form>
                </c:if>
            </div>
        </div>

        <c:if test="${not empty param.msg}">
            <div style="background-color: var(--emerald-100); color: var(--emerald-600); padding: 0.85rem 1.25rem; border-radius: 8px; margin-bottom: 1.5rem; font-weight: 600;">
                ✓ ${param.msg}
            </div>
        </c:if>
        <c:if test="${not empty param.err}">
            <div style="background-color: var(--rose-100); color: var(--rose-600); padding: 0.85rem 1.25rem; border-radius: 8px; margin-bottom: 1.5rem; font-weight: 600;">
                ✕ ${param.err}
            </div>
        </c:if>

        <!-- Visual Tracking Stepper -->
        <c:choose>
            <c:when test="${order.orderStatus == 'CANCELLED'}">
                <div style="background-color: var(--rose-100); color: var(--rose-600); padding: 1.25rem; border-radius: var(--radius-lg); text-align: center; font-weight: 800; font-size: 1.15rem; margin-bottom: 2.5rem;">
                    ✕ This order has been cancelled. Any pre-authorized charges have been scheduled for full refund.
                </div>
            </c:when>
            <c:otherwise>
                <div class="stepper-container">
                    <div class="stepper-progress-bar">
                        <div class="stepper-progress-fill" style="width: ${order.orderStatus == 'DELIVERED' ? '100%' : (order.orderStatus == 'SHIPPED' ? '75%' : (order.orderStatus == 'PACKED' ? '50%' : (order.orderStatus == 'CONFIRMED' ? '25%' : '0%')))};"></div>
                    </div>
                    <div class="step-node">
                        <div class="step-circle active">✓</div>
                        <div class="step-label">Placed</div>
                    </div>
                    <div class="step-node">
                        <div class="step-circle ${order.orderStatus == 'CONFIRMED' || order.orderStatus == 'PACKED' || order.orderStatus == 'SHIPPED' || order.orderStatus == 'DELIVERED' ? 'active' : ''}">2</div>
                        <div class="step-label">Confirmed</div>
                    </div>
                    <div class="step-node">
                        <div class="step-circle ${order.orderStatus == 'PACKED' || order.orderStatus == 'SHIPPED' || order.orderStatus == 'DELIVERED' ? 'active' : ''}">3</div>
                        <div class="step-label">Packed</div>
                    </div>
                    <div class="step-node">
                        <div class="step-circle ${order.orderStatus == 'SHIPPED' || order.orderStatus == 'DELIVERED' ? 'active' : ''}">4</div>
                        <div class="step-label">Shipped</div>
                    </div>
                    <div class="step-node">
                        <div class="step-circle ${order.orderStatus == 'DELIVERED' ? 'active' : ''}">5</div>
                        <div class="step-label">Delivered</div>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>

        <!-- Details Grid -->
        <div style="display: grid; grid-template-columns: 1.6fr 1fr; gap: 2rem; margin-bottom: 3rem;">
            <!-- Items Box -->
            <div style="background-color: #ffffff; border-radius: var(--radius-xl); border: 1px solid var(--slate-200); padding: 1.75rem;">
                <h3 style="font-size: 1.15rem; font-weight: 800; color: var(--navy-800); margin-bottom: 1.5rem; border-bottom: 1px solid var(--slate-100); padding-bottom: 0.75rem;">
                    Ordered Items (${order.items.size()})
                </h3>

                <div style="display: flex; flex-direction: column; gap: 1.5rem;">
                    <c:forEach var="item" items="${order.items}">
                        <div style="display: flex; gap: 1.25rem; align-items: center; border-bottom: 1px solid var(--slate-100); padding-bottom: 1rem;">
                            <img src="${item.productImageUrl}" style="width: 75px; height: 75px; object-fit: cover; border-radius: 8px;">
                            <div style="flex: 1;">
                                <a href="${pageContext.request.contextPath}/product?id=${item.productId}" style="font-weight: 700; color: var(--navy-800); font-size: 1.05rem;">
                                    ${item.productName}
                                </a>
                                <div style="font-size: 0.85rem; color: var(--slate-500); margin-top: 0.2rem;">
                                    Sold by: <strong>${item.sellerStoreName}</strong>
                                </div>
                                <div style="font-size: 0.85rem; color: var(--slate-500);">
                                    Qty: ${item.quantity} × ${item.formattedUnitPrice}
                                </div>
                            </div>
                            <div style="text-align: right; font-weight: 800; font-size: 1.15rem; color: var(--navy-800);">
                                ${item.formattedTotalPrice}
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>

            <!-- Address & Summary -->
            <div style="display: flex; flex-direction: column; gap: 1.5rem;">
                <div style="background-color: #ffffff; border-radius: var(--radius-xl); border: 1px solid var(--slate-200); padding: 1.5rem;">
                    <h4 style="font-size: 1rem; font-weight: 800; color: var(--navy-800); margin-bottom: 0.75rem;">Delivery Address</h4>
                    <c:choose>
                        <c:when test="${not empty order.shippingAddress}">
                            <div style="font-weight: 700; color: var(--slate-800);">${order.shippingAddress.fullName}</div>
                            <div style="font-size: 0.9rem; color: var(--slate-600); margin-top: 0.25rem;">
                                ${order.shippingAddress.formattedSingleLine}
                            </div>
                            <div style="font-size: 0.85rem; color: var(--slate-500); margin-top: 0.25rem;">
                                Phone: ${order.shippingAddress.phone}
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div style="color: var(--slate-500);">Registered Delivery Address</div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div style="background-color: #ffffff; border-radius: var(--radius-xl); border: 1px solid var(--slate-200); padding: 1.5rem;">
                    <h4 style="font-size: 1rem; font-weight: 800; color: var(--navy-800); margin-bottom: 0.75rem;">Payment Information</h4>
                    <div style="font-size: 0.9rem; color: var(--slate-700); margin-bottom: 0.35rem;">
                        Method: <strong>${order.paymentMethod}</strong>
                    </div>
                    <div style="font-size: 0.9rem; color: var(--slate-700); margin-bottom: 1rem;">
                        Status: <strong>${order.paymentStatus}</strong>
                    </div>

                    <div style="border-top: 1px solid var(--slate-200); padding-top: 0.75rem; font-size: 0.9rem;">
                        <div class="summary-line"><span>Subtotal:</span> <span>${order.formattedSubtotal}</span></div>
                        <c:if test="${order.discount > 0}">
                            <div class="summary-line" style="color: var(--emerald-600);"><span>Discount:</span> <span>-${order.formattedDiscount}</span></div>
                        </c:if>
                        <div class="summary-line"><span>Estimated Tax:</span> <span>${order.formattedTax}</span></div>
                        <div class="summary-line"><span>Shipping:</span> <span>${order.formattedShipping}</span></div>
                        <div style="border-top: 2px dashed var(--slate-200); margin-top: 0.75rem; padding-top: 0.75rem; display: flex; justify-content: space-between; font-weight: 900; font-size: 1.25rem; color: var(--navy-800);">
                            <span>Total Paid:</span> <span>${order.formattedTotal}</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </main>

    <jsp:include page="includes/footer.jsp"/>

</body>
</html>
