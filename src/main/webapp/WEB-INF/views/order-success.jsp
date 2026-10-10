<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Order Confirmed — HariMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/responsive.css">
</head>
<body>

    <jsp:include page="includes/header.jsp"/>

    <main class="container" style="padding-top: 3.5rem; padding-bottom: 5rem;">

        <div style="background-color: #ffffff; border-radius: var(--radius-xl); border: 1px solid var(--slate-200); padding: 3rem; max-width: 780px; margin: 0 auto; text-align: center; box-shadow: var(--shadow-md);">
            <div style="width: 72px; height: 72px; background-color: var(--emerald-100); color: var(--emerald-600); border-radius: var(--radius-full); display: flex; align-items: center; justify-content: center; font-size: 2.25rem; margin: 0 auto 1.5rem; font-weight: 800;">
                ✓
            </div>

            <h1 style="font-size: 2rem; font-weight: 900; color: var(--navy-800); margin-bottom: 0.5rem;">
                Thank You! Your Order is Placed.
            </h1>
            <p style="color: var(--slate-500); font-size: 1rem; margin-bottom: 2rem;">
                We've sent an order confirmation to your registered email and phone number.
            </p>

            <div style="background-color: var(--ivory-50); border: 1px solid var(--slate-200); border-radius: var(--radius-lg); padding: 1.5rem; text-align: left; margin-bottom: 2rem;">
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; font-size: 0.95rem;">
                    <div>
                        <div style="color: var(--slate-400); font-size: 0.8rem; text-transform: uppercase; font-weight: 700;">Order Reference</div>
                        <div style="font-weight: 800; color: var(--navy-800); font-size: 1.15rem; margin-top: 0.2rem;">${order.orderNumber}</div>
                    </div>
                    <div>
                        <div style="color: var(--slate-400); font-size: 0.8rem; text-transform: uppercase; font-weight: 700;">Payment Method</div>
                        <div style="font-weight: 700; color: var(--slate-700); margin-top: 0.2rem;">${order.paymentMethod} (${order.paymentStatus})</div>
                    </div>
                    <div>
                        <div style="color: var(--slate-400); font-size: 0.8rem; text-transform: uppercase; font-weight: 700;">Estimated Delivery</div>
                        <div style="font-weight: 700; color: var(--emerald-600); margin-top: 0.2rem;">Within 3–5 Business Days</div>
                    </div>
                    <div>
                        <div style="color: var(--slate-400); font-size: 0.8rem; text-transform: uppercase; font-weight: 700;">Total Amount Paid</div>
                        <div style="font-weight: 800; color: var(--navy-800); font-size: 1.15rem; margin-top: 0.2rem;">${order.formattedTotal}</div>
                    </div>
                </div>
            </div>

            <div style="display: flex; gap: 1rem; justify-content: center; flex-wrap: wrap;">
                <a href="${pageContext.request.contextPath}/orders/detail?id=${order.id}" class="btn-primary" style="padding: 0.85rem 1.75rem;">
                    📦 Track This Order
                </a>
                <a href="${pageContext.request.contextPath}/" class="btn-secondary" style="background-color: var(--slate-100); color: var(--slate-700); border: 1px solid var(--slate-300);">
                    🛍️ Continue Shopping
                </a>
            </div>
        </div>

    </main>

    <jsp:include page="includes/footer.jsp"/>

</body>
</html>
