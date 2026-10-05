/**
 * HariMart 3-Step Checkout Controller
 */

let checkoutData = {
    selectedAddressId: null,
    newAddress: null,
    paymentMethod: 'COD',
    couponCode: '',
    directProductId: null,
    directQuantity: 1
};

let cartSummary = null;

document.addEventListener('DOMContentLoaded', async () => {
    if (!window.Auth.isAuthenticated()) {
        window.UI.showToast('Please sign in to complete checkout', 'info');
        window.location.href = `login.html?redirect=${encodeURIComponent(window.location.pathname + window.location.search)}`;
        return;
    }

    parseDirectParams();
    await loadCustomerAddresses();
    await loadOrderSummary();
    setupCheckoutStepNavigation();
});

function parseDirectParams() {
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('direct') === 'true' && urlParams.get('product')) {
        checkoutData.directProductId = parseInt(urlParams.get('product'));
        checkoutData.directQuantity = parseInt(urlParams.get('qty') || '1');
    }
}

async function loadCustomerAddresses() {
    const container = document.getElementById('saved-addresses-list');
    if (!container) return;

    try {
        const res = await window.api.get('/api/users/addresses');
        if (res.success && res.data && res.data.length > 0) {
            const addresses = res.data;
            checkoutData.selectedAddressId = addresses[0].id;

            container.innerHTML = addresses.map((a, idx) => `
                <label class="address-radio-card ${idx === 0 ? 'selected' : ''}" style="display:block; border:2px solid ${idx === 0 ? 'var(--primary)' : 'var(--border-color)'}; border-radius:var(--radius-md); padding:1rem; margin-bottom:0.75rem; cursor:pointer;">
                    <div style="display:flex; align-items:flex-start; gap:0.75rem;">
                        <input type="radio" name="checkout_address" value="${a.id}" ${idx === 0 ? 'checked' : ''} onchange="selectAddress(${a.id}, this)">
                        <div>
                            <strong>${a.addressLine}</strong>
                            <p style="color:var(--text-secondary); font-size:0.875rem; margin-top:0.2rem;">
                                ${a.city}, ${a.state} - <strong>${a.pincode}</strong> (${a.country})
                            </p>
                            ${a.isDefault ? '<span class="status-pill active" style="margin-top:0.35rem;">Default</span>' : ''}
                        </div>
                    </div>
                </label>
            `).join('');
        } else {
            container.innerHTML = `<p style="color:var(--text-muted); font-size:0.875rem; margin-bottom:1rem;">No saved addresses found. Please enter a delivery address below.</p>`;
            document.getElementById('new-address-form-wrap').style.display = 'block';
        }
    } catch (e) {
        console.warn(e);
    }
}

function selectAddress(id, radioEl) {
    checkoutData.selectedAddressId = id;
    checkoutData.newAddress = null;
    document.querySelectorAll('.address-radio-card').forEach(c => c.style.borderColor = 'var(--border-color)');
    radioEl.closest('.address-radio-card').style.borderColor = 'var(--primary)';
}

async function loadOrderSummary() {
    const itemsListEl = document.getElementById('checkout-items-list');
    const subtotalEl = document.getElementById('summary-subtotal');
    const discountEl = document.getElementById('summary-discount');
    const deliveryEl = document.getElementById('summary-delivery');
    const totalEl = document.getElementById('summary-final-total');

    try {
        if (checkoutData.directProductId) {
            // Direct buy now item summary
            const res = await window.api.get(`/api/products/${checkoutData.directProductId}`);
            if (res.success && res.data) {
                const p = res.data;
                const unitPrice = p.finalPrice || p.price;
                const subtotal = unitPrice * checkoutData.directQuantity;
                const delivery = subtotal >= 499 ? 0 : 49;
                const total = subtotal + delivery;

                itemsListEl.innerHTML = `
                    <div style="display:flex; align-items:center; gap:0.85rem; padding:0.75rem 0; border-bottom:1px solid var(--border-color);">
                        <img src="${p.imageUrl}" alt="${p.name}" style="width:50px; height:50px; object-fit:cover; border-radius:var(--radius-sm);">
                        <div style="flex:1;">
                            <div style="font-weight:600; font-size:0.85rem; color:var(--text-primary);">${p.name}</div>
                            <div style="font-size:0.75rem; color:var(--text-muted);">Qty: ${checkoutData.directQuantity} × ${window.UI.formatCurrency(unitPrice)}</div>
                        </div>
                        <div style="font-weight:700; color:var(--text-primary);">${window.UI.formatCurrency(subtotal)}</div>
                    </div>
                `;

                subtotalEl.textContent = window.UI.formatCurrency(subtotal);
                discountEl.textContent = '₹0.00';
                deliveryEl.textContent = delivery === 0 ? 'FREE' : window.UI.formatCurrency(delivery);
                totalEl.textContent = window.UI.formatCurrency(total);
            }
        } else {
            // Cart summary
            const cart = await window.Cart.getCart(checkoutData.couponCode);
            if (cart) {
                cartSummary = cart;
                itemsListEl.innerHTML = cart.items.map(i => `
                    <div style="display:flex; align-items:center; gap:0.85rem; padding:0.75rem 0; border-bottom:1px solid var(--border-color);">
                        <img src="${i.productImage}" alt="${i.productName}" style="width:50px; height:50px; object-fit:cover; border-radius:var(--radius-sm);">
                        <div style="flex:1;">
                            <div style="font-weight:600; font-size:0.85rem; color:var(--text-primary);">${i.productName}</div>
                            <div style="font-size:0.75rem; color:var(--text-muted);">Qty: ${i.quantity} × ${window.UI.formatCurrency(i.unitPrice)}</div>
                        </div>
                        <div style="font-weight:700; color:var(--text-primary);">${window.UI.formatCurrency(i.itemTotal)}</div>
                    </div>
                `).join('');

                subtotalEl.textContent = window.UI.formatCurrency(cart.subtotal);
                discountEl.textContent = `-${window.UI.formatCurrency(cart.discount)}`;
                deliveryEl.textContent = cart.deliveryCharge === 0 ? 'FREE' : window.UI.formatCurrency(cart.deliveryCharge);
                totalEl.textContent = window.UI.formatCurrency(cart.finalTotal);
            }
        }
    } catch (e) {
        console.warn(e);
    }
}

function setupCheckoutStepNavigation() {
    // Step 1 -> Step 2
    document.getElementById('to-payment-step-btn').addEventListener('click', () => {
        // Check if user filled new address form
        const line = document.getElementById('new-address-line').value.trim();
        const city = document.getElementById('new-address-city').value.trim();
        const state = document.getElementById('new-address-state').value.trim();
        const pin = document.getElementById('new-address-pincode').value.trim();

        if (line && city && pin) {
            checkoutData.newAddress = {
                addressLine: line,
                city: city,
                state: state,
                pincode: pin,
                country: 'India',
                isDefault: false
            };
            checkoutData.selectedAddressId = null;
        } else if (!checkoutData.selectedAddressId) {
            window.UI.showToast('Please select or enter a delivery address', 'warning');
            return;
        }

        switchCheckoutStep(2);
    });

    // Step 2 -> Step 3
    document.getElementById('to-review-step-btn').addEventListener('click', () => {
        const paymentRadio = document.querySelector('input[name="payment_method_radio"]:checked');
        if (paymentRadio) {
            checkoutData.paymentMethod = paymentRadio.value;
        }
        switchCheckoutStep(3);
    });

    // Back Buttons
    document.getElementById('back-to-address-btn').addEventListener('click', () => switchCheckoutStep(1));
    document.getElementById('back-to-payment-btn').addEventListener('click', () => switchCheckoutStep(2));

    // Payment Radio Switcher Simulation
    document.querySelectorAll('input[name="payment_method_radio"]').forEach(r => {
        r.addEventListener('change', (e) => {
            const val = e.target.value;
            const onlineDetails = document.getElementById('online-payment-details');
            if (onlineDetails) {
                onlineDetails.style.display = val === 'ONLINE_MOCK' ? 'block' : 'none';
            }
        });
    });

    // Place Order Button
    document.getElementById('place-order-btn').addEventListener('click', placeOrder);

    // Apply coupon in checkout
    const couponBtn = document.getElementById('apply-coupon-btn');
    if (couponBtn) {
        couponBtn.addEventListener('click', async () => {
            const code = document.getElementById('coupon-input').value.trim();
            checkoutData.couponCode = code;
            await loadOrderSummary();
            window.UI.showToast(`Applied coupon: ${code.toUpperCase()}`, 'success');
        });
    }
}

function switchCheckoutStep(step) {
    document.querySelectorAll('.checkout-step-content').forEach(s => s.style.display = 'none');
    document.querySelectorAll('.step-indicator-item').forEach(ind => ind.classList.remove('active'));

    document.getElementById(`step-${step}-content`).style.display = 'block';
    document.getElementById(`step-ind-${step}`).classList.add('active');
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

async function placeOrder() {
    const btn = document.getElementById('place-order-btn');
    btn.disabled = true;
    btn.innerHTML = 'Placing your order... ⏳';

    const orderPayload = {
        addressId: checkoutData.selectedAddressId,
        newAddress: checkoutData.newAddress,
        paymentMethod: checkoutData.paymentMethod,
        couponCode: checkoutData.couponCode,
        directProductId: checkoutData.directProductId,
        directQuantity: checkoutData.directQuantity
    };

    try {
        const res = await window.api.post('/api/orders', orderPayload);
        if (res.success && res.data) {
            window.UI.showToast('🎉 Order placed successfully!', 'success');
            setTimeout(() => {
                window.location.href = `order-tracking.html?orderNumber=${res.data.orderNumber}`;
            }, 800);
        }
    } catch (err) {
        window.UI.showToast(err.message || 'Failed to place order. Please try again.', 'error');
        btn.disabled = false;
        btn.innerHTML = 'Place Order & Pay';
    }
}
