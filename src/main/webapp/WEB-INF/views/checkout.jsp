<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Secure Checkout — HariMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/responsive.css">
    <style>
        .checkout-grid {
            display: grid;
            grid-template-columns: 1.6fr 1fr;
            gap: 2.5rem;
            margin-top: 2rem;
            margin-bottom: 5rem;
        }
        .checkout-box {
            background-color: #ffffff;
            border-radius: var(--radius-xl);
            border: 1px solid var(--slate-200);
            padding: 2rem;
            box-shadow: var(--shadow-sm);
            margin-bottom: 2rem;
        }
        .step-heading {
            display: flex;
            align-items: center;
            gap: 0.75rem;
            font-size: 1.25rem;
            font-weight: 800;
            color: var(--navy-800);
            margin-bottom: 1.5rem;
            border-bottom: 1px solid var(--slate-100);
            padding-bottom: 0.75rem;
        }
        .step-badge {
            width: 32px;
            height: 32px;
            background-color: var(--amber-400);
            color: var(--navy-900);
            border-radius: var(--radius-full);
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 800;
            font-size: 0.95rem;
        }
        .address-card-radio {
            border: 1px solid var(--slate-200);
            border-radius: var(--radius-md);
            padding: 1rem;
            margin-bottom: 1rem;
            display: flex;
            gap: 1rem;
            cursor: pointer;
            transition: border-color var(--transition-fast);
        }
        .address-card-radio:hover, .address-card-radio.selected {
            border-color: var(--navy-600);
            background-color: var(--ivory-50);
        }
        .payment-option-card {
            border: 1px solid var(--slate-200);
            border-radius: var(--radius-md);
            padding: 1rem;
            margin-bottom: 1rem;
            display: flex;
            gap: 1rem;
            align-items: center;
            cursor: pointer;
        }
        .payment-option-card:hover {
            border-color: var(--amber-400);
        }
        @media(max-width: 900px) {
            .checkout-grid { grid-template-columns: 1fr; }
        }
    </style>
</head>
<body>

    <jsp:include page="includes/header.jsp"/>

    <main class="container" style="padding-top: 2rem;">

        <div style="text-align: center; margin-bottom: 2rem;">
            <h1 style="font-size: 2rem; font-weight: 800; color: var(--navy-800);">Fast & Secure Checkout</h1>
            <p style="color: var(--slate-500); font-size: 0.95rem;">Complete your order in 3 simple steps</p>
        </div>

        <c:if test="${not empty errorMessage}">
            <div style="background-color: var(--rose-100); color: var(--rose-600); padding: 1rem 1.25rem; border-radius: 8px; margin-bottom: 2rem; font-weight: 600;">
                ✕ ${errorMessage}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/checkout" method="POST" id="checkoutForm">

            <c:if test="${not empty directProduct}">
                <input type="hidden" name="directProductId" value="${directProduct.id}">
                <input type="hidden" name="directQuantity" value="${directQuantity}">
            </c:if>

            <div class="checkout-grid">
                <!-- Left: Steps 1 & 2 -->
                <div>
                    <!-- Step 1: Delivery Address -->
                    <div class="checkout-box">
                        <div class="step-heading">
                            <span class="step-badge">1</span>
                            <span>Select Delivery Address</span>
                        </div>

                        <c:if test="${not empty addresses}">
                            <div style="margin-bottom: 1.5rem;">
                                <c:forEach var="addr" items="${addresses}">
                                    <label class="address-card-radio">
                                        <input type="radio" name="addressId" value="${addr.id}" ${addr.isDefault ? 'checked' : ''} required>
                                        <div>
                                            <div style="font-weight: 700; color: var(--navy-800);">${addr.fullName} <span style="font-size: 0.85rem; color: var(--slate-500); font-weight: normal;">(${addr.phone})</span></div>
                                            <div style="font-size: 0.9rem; color: var(--slate-600); margin-top: 0.25rem;">${addr.formattedSingleLine}</div>
                                            <c:if test="${addr.isDefault}">
                                                <span style="font-size: 0.75rem; background: var(--emerald-100); color: var(--emerald-600); padding: 0.15rem 0.5rem; border-radius: 4px; font-weight: 700; display: inline-block; margin-top: 0.35rem;">Default Address</span>
                                            </c:if>
                                        </div>
                                    </label>
                                </c:forEach>
                            </div>
                        </c:if>

                        <div style="border-top: 1px dashed var(--slate-200); padding-top: 1.25rem;">
                            <label style="font-weight: 700; font-size: 0.95rem; color: var(--navy-800); display: flex; align-items: center; gap: 0.5rem; cursor: pointer; margin-bottom: 1rem;">
                                <input type="radio" name="addressId" value="new" ${empty addresses ? 'checked' : ''} onclick="document.getElementById('newAddressFields').style.display='block'">
                                <span>+ Add a New Delivery Address</span>
                            </label>

                            <div id="newAddressFields" style="${empty addresses ? 'display:block;' : 'display:none;'}">
                                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 1rem;">
                                    <div>
                                        <label class="form-label">Full Name</label>
                                        <input type="text" name="fullName" class="form-control" placeholder="Recipient's Name">
                                    </div>
                                    <div>
                                        <label class="form-label">Mobile Number</label>
                                        <input type="text" name="phone" class="form-control" placeholder="10-digit mobile number">
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label class="form-label">Flat, House no., Building, Apartment</label>
                                    <input type="text" name="addressLine1" class="form-control" placeholder="Address Line 1">
                                </div>
                                <div class="form-group">
                                    <label class="form-label">Area, Colony, Street, Sector, Village</label>
                                    <input type="text" name="addressLine2" class="form-control" placeholder="Address Line 2 (Optional)">
                                </div>
                                <div style="display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 1rem;">
                                    <div>
                                        <label class="form-label">Town / City</label>
                                        <input type="text" name="city" class="form-control" placeholder="City">
                                    </div>
                                    <div>
                                        <label class="form-label">State</label>
                                        <input type="text" name="state" class="form-control" placeholder="State">
                                    </div>
                                    <div>
                                        <label class="form-label">Pincode</label>
                                        <input type="text" name="pincode" class="form-control" placeholder="6-digit PIN">
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Step 2: Payment Method -->
                    <div class="checkout-box">
                        <div class="step-heading">
                            <span class="step-badge">2</span>
                            <span>Select Payment Method</span>
                        </div>

                        <div style="background-color: var(--ivory-100); padding: 0.75rem 1rem; border-radius: 8px; font-size: 0.85rem; color: var(--slate-600); margin-bottom: 1.5rem;">
                            ℹ️ <strong>Demo Simulation:</strong> Payments are processed safely in demo simulation mode without charging real money.
                        </div>

                        <label class="payment-option-card">
                            <input type="radio" name="paymentMethod" value="UPI" checked>
                            <div>
                                <div style="font-weight: 700; color: var(--navy-800);">Instant UPI / QR Code (Simulated)</div>
                                <div style="font-size: 0.85rem; color: var(--slate-500);">Google Pay, PhonePe, Paytm, BHIM</div>
                            </div>
                        </label>

                        <label class="payment-option-card">
                            <input type="radio" name="paymentMethod" value="CARD">
                            <div>
                                <div style="font-weight: 700; color: var(--navy-800);">Credit / Debit Card (Simulated)</div>
                                <div style="font-size: 0.85rem; color: var(--slate-500);">Visa, MasterCard, RuPay, Diners Club</div>
                            </div>
                        </label>

                        <label class="payment-option-card">
                            <input type="radio" name="paymentMethod" value="COD">
                            <div>
                                <div style="font-weight: 700; color: var(--navy-800);">Cash on Delivery (COD)</div>
                                <div style="font-size: 0.85rem; color: var(--slate-500);">Pay in cash or UPI directly to delivery agent</div>
                            </div>
                        </label>
                    </div>
                </div>

                <!-- Right: Step 3 Recap & Place Order -->
                <div>
                    <div class="checkout-box">
                        <div class="step-heading">
                            <span class="step-badge">3</span>
                            <span>Order Recap</span>
                        </div>

                        <div style="margin-bottom: 1.5rem;">
                            <c:choose>
                                <c:when test="${not empty directProduct}">
                                    <div style="display: flex; gap: 1rem; align-items: center; margin-bottom: 1rem;">
                                        <img src="${directProduct.imageUrl}" style="width: 60px; height: 60px; object-fit: cover; border-radius: 6px;">
                                        <div style="flex: 1;">
                                            <div style="font-weight: 700; font-size: 0.95rem; color: var(--navy-800);">${directProduct.name}</div>
                                            <div style="font-size: 0.85rem; color: var(--slate-500);">Qty: ${directQuantity} × ${directProduct.formattedPrice}</div>
                                        </div>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="item" items="${cartItems}">
                                        <div style="display: flex; gap: 1rem; align-items: center; margin-bottom: 1rem; border-bottom: 1px solid var(--slate-100); padding-bottom: 0.75rem;">
                                            <img src="${item.product.imageUrl}" style="width: 50px; height: 50px; object-fit: cover; border-radius: 6px;">
                                            <div style="flex: 1;">
                                                <div style="font-weight: 700; font-size: 0.9rem; color: var(--navy-800);">${item.product.name}</div>
                                                <div style="font-size: 0.8rem; color: var(--slate-500);">Qty: ${item.quantity} × ${item.product.formattedPrice}</div>
                                            </div>
                                            <div style="font-weight: 700; font-size: 0.9rem;">${item.formattedItemTotal}</div>
                                        </div>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <div style="border-top: 1px solid var(--slate-200); padding-top: 1rem; margin-bottom: 1.5rem;">
                            <div class="summary-line">
                                <span>Subtotal</span>
                                <span>${totals.formattedSubtotal}</span>
                            </div>
                            <c:if test="${totals.discount > 0}">
                                <div class="summary-line" style="color: var(--emerald-600);">
                                    <span>Discount (HARI10)</span>
                                    <span>-${totals.formattedDiscount}</span>
                                </div>
                            </c:if>
                            <div class="summary-line">
                                <span>Estimated GST (5%)</span>
                                <span>${totals.formattedTax}</span>
                            </div>
                            <div class="summary-line">
                                <span>Delivery Fee</span>
                                <span>${totals.formattedShipping}</span>
                            </div>
                            <div style="border-top: 2px dashed var(--slate-200); margin-top: 1rem; padding-top: 1rem; display: flex; justify-content: space-between; align-items: baseline;">
                                <span style="font-size: 1.15rem; font-weight: 800; color: var(--navy-800);">Order Total</span>
                                <span style="font-size: 1.75rem; font-weight: 900; color: var(--navy-800);">${totals.formattedTotal}</span>
                            </div>
                        </div>

                        <button type="submit" class="btn-primary" style="width: 100%; justify-content: center; padding: 1rem; font-size: 1.1rem; box-shadow: 0 4px 15px rgba(255,181,71,0.5);">
                            ✓ Place Your Order Now
                        </button>

                        <div style="text-align: center; font-size: 0.8rem; color: var(--slate-500); margin-top: 1rem;">
                            By placing your order, you agree to HariMart's conditions of use and sale.
                        </div>
                    </div>
                </div>
            </div>
        </form>

    </main>

    <jsp:include page="includes/footer.jsp"/>

</body>
</html>
