<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Shopping Cart — HariMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/responsive.css">
    <style>
        .cart-layout {
            display: grid;
            grid-template-columns: 1.6fr 0.9fr;
            gap: 2.5rem;
            margin-top: 1.5rem;
            margin-bottom: 4rem;
        }
        .cart-items-card {
            background-color: #ffffff;
            border-radius: var(--radius-xl);
            border: 1px solid var(--slate-200);
            padding: 1.75rem;
            box-shadow: var(--shadow-sm);
        }
        .cart-item-row {
            display: grid;
            grid-template-columns: 100px 1fr auto;
            gap: 1.5rem;
            padding: 1.5rem 0;
            border-bottom: 1px solid var(--slate-100);
            align-items: center;
        }
        .summary-card {
            background-color: #ffffff;
            border-radius: var(--radius-xl);
            border: 1px solid var(--slate-200);
            padding: 1.75rem;
            box-shadow: var(--shadow-sm);
            height: fit-content;
        }
        .summary-line {
            display: flex;
            justify-content: space-between;
            margin-bottom: 0.85rem;
            font-size: 0.95rem;
            color: var(--slate-600);
        }
        .qty-stepper {
            display: inline-flex;
            align-items: center;
            border: 1px solid var(--slate-300);
            border-radius: 8px;
            overflow: hidden;
        }
        .qty-stepper button {
            background-color: var(--slate-100);
            border: none;
            width: 32px;
            height: 32px;
            font-weight: 700;
            cursor: pointer;
        }
        .qty-stepper input {
            width: 44px;
            height: 32px;
            border: none;
            text-align: center;
            font-weight: 700;
        }
        @media(max-width: 900px) {
            .cart-layout { grid-template-columns: 1fr; }
            .cart-item-row { grid-template-columns: 80px 1fr; }
        }
    </style>
</head>
<body>

    <jsp:include page="includes/header.jsp"/>

    <main class="container" style="padding-top: 2rem;">

        <h1 style="font-size: 1.85rem; font-weight: 800; color: var(--navy-800); margin-bottom: 0.5rem;">
            Shopping Cart
        </h1>

        <c:choose>
            <c:when test="${not empty cartItems}">
                <div class="cart-layout">
                    <!-- Left: Cart Items -->
                    <div class="cart-items-card">
                        <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid var(--slate-200); padding-bottom: 1rem; margin-bottom: 0.5rem;">
                            <h3 style="font-size: 1.15rem; font-weight: 700; color: var(--navy-800);">Items in Cart (${cartItems.size()})</h3>
                            <form action="${pageContext.request.contextPath}/cart/clear" method="POST" style="margin: 0;">
                                <button type="submit" style="background: none; border: none; color: var(--rose-600); font-size: 0.85rem; font-weight: 600; cursor: pointer;">
                                    Clear Cart
                                </button>
                            </form>
                        </div>

                        <c:forEach var="item" items="${cartItems}">
                            <div class="cart-item-row" id="cartRow-${item.id}">
                                <img src="${item.product.imageUrl}" alt="${item.product.name}" style="width: 100px; height: 100px; object-fit: cover; border-radius: 8px;">
                                <div>
                                    <div style="font-size: 0.8rem; color: var(--slate-400); font-weight: 700; text-transform: uppercase;">
                                        ${item.product.brand} • <span style="color: var(--navy-600);">${item.product.sellerStoreName}</span>
                                    </div>
                                    <a href="${pageContext.request.contextPath}/product?id=${item.product.id}" style="font-weight: 700; font-size: 1.05rem; color: var(--navy-800); display: block; margin: 0.25rem 0;">
                                        ${item.product.name}
                                    </a>
                                    <div style="font-weight: 800; color: var(--navy-800); font-size: 1.15rem; margin-bottom: 0.75rem;">
                                        ${item.product.formattedPrice}
                                    </div>
                                    <div style="display: flex; align-items: center; gap: 1rem;">
                                        <div class="qty-stepper">
                                            <button type="button" onclick="updateCartItemQuantity(${item.id}, ${item.quantity - 1}, '${pageContext.request.contextPath}')">-</button>
                                            <input type="text" id="cartQtyInput-${item.id}" value="${item.quantity}" readonly>
                                            <button type="button" onclick="updateCartItemQuantity(${item.id}, ${item.quantity + 1}, '${pageContext.request.contextPath}')">+</button>
                                        </div>
                                        <button type="button" class="btn-action-sm" onclick="updateCartItemQuantity(${item.id}, 0, '${pageContext.request.contextPath}')" style="color: var(--rose-600);">
                                            Remove
                                        </button>
                                    </div>
                                </div>
                                <div style="text-align: right; font-weight: 800; font-size: 1.15rem; color: var(--navy-800);">
                                    ${item.formattedItemTotal}
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <!-- Right: Summary Box -->
                    <div class="summary-card">
                        <h3 style="font-size: 1.25rem; font-weight: 800; color: var(--navy-800); margin-bottom: 1.5rem; border-bottom: 1px solid var(--slate-200); padding-bottom: 0.75rem;">
                            Order Summary
                        </h3>

                        <!-- Promo Code Form -->
                        <form action="${pageContext.request.contextPath}/cart/apply-coupon" method="POST" style="margin-bottom: 1.5rem;">
                            <label style="display: block; font-size: 0.85rem; font-weight: 600; color: var(--slate-700); margin-bottom: 0.4rem;">Promotional Coupon</label>
                            <div style="display: flex; gap: 0.5rem;">
                                <input type="text" name="coupon" placeholder="e.g. HARI10" value="${totals.couponCode}" class="form-control" style="text-transform: uppercase;">
                                <button type="submit" class="btn-primary" style="padding: 0 1rem; font-size: 0.85rem; border-radius: 8px;">Apply</button>
                            </div>
                            <c:if test="${totals.couponApplied}">
                                <div style="font-size: 0.8rem; color: var(--emerald-600); font-weight: 600; margin-top: 0.4rem;">
                                    ✓ Coupon HARI10 applied! 10% discount subtracted.
                                </div>
                            </c:if>
                            <c:if test="${not empty couponError}">
                                <div style="font-size: 0.8rem; color: var(--rose-600); font-weight: 600; margin-top: 0.4rem;">
                                    ${couponError}
                                </div>
                            </c:if>
                        </form>

                        <div class="summary-line">
                            <span>Subtotal</span>
                            <span id="cartSubtotal" style="font-weight: 700;">${totals.formattedSubtotal}</span>
                        </div>
                        <c:if test="${totals.discount > 0}">
                            <div class="summary-line" style="color: var(--emerald-600);">
                                <span>Discount (HARI10)</span>
                                <span id="cartDiscount" style="font-weight: 700;">-${totals.formattedDiscount}</span>
                            </div>
                        </c:if>
                        <div class="summary-line">
                            <span>Estimated GST (5%)</span>
                            <span id="cartTax">${totals.formattedTax}</span>
                        </div>
                        <div class="summary-line">
                            <span>Delivery Fee</span>
                            <span id="cartShipping">${totals.formattedShipping}</span>
                        </div>

                        <div style="border-top: 2px dashed var(--slate-200); margin: 1.25rem 0; padding-top: 1rem; display: flex; justify-content: space-between; align-items: baseline;">
                            <span style="font-size: 1.15rem; font-weight: 800; color: var(--navy-800);">Total Payable</span>
                            <span id="cartTotal" style="font-size: 1.65rem; font-weight: 900; color: var(--navy-800);">${totals.formattedTotal}</span>
                        </div>

                        <a href="${pageContext.request.contextPath}/checkout" class="btn-primary" style="width: 100%; justify-content: center; padding: 0.95rem; font-size: 1rem; margin-top: 1rem;">
                            🔒 Proceed to Checkout
                        </a>

                        <div style="text-align: center; font-size: 0.8rem; color: var(--slate-400); margin-top: 1rem;">
                            Safe & 256-bit Encrypted Checkout
                        </div>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div style="background-color: #ffffff; padding: 4rem 2rem; text-align: center; border-radius: var(--radius-xl); border: 1px solid var(--slate-200); margin: 2rem 0 5rem;">
                    <div style="font-size: 3.5rem; margin-bottom: 1rem;">🛒</div>
                    <h2 style="font-size: 1.6rem; font-weight: 800; color: var(--navy-800);">Your Shopping Cart is Empty</h2>
                    <p style="color: var(--slate-500); margin-top: 0.5rem; max-width: 450px; margin-left: auto; margin-right: auto;">
                        Looks like you haven't added anything to your cart yet. Explore our bestselling categories and flash deals!
                    </p>
                    <a href="${pageContext.request.contextPath}/" class="btn-primary" style="margin-top: 2rem; display: inline-flex;">
                        🛍️ Start Shopping Now
                    </a>
                </div>
            </c:otherwise>
        </c:choose>

    </main>

    <jsp:include page="includes/footer.jsp"/>

</body>
</html>
