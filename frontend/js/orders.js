/**
 * HariMart Order Tracking & Details Controller
 */

let currentOrder = null;

document.addEventListener('DOMContentLoaded', async () => {
    if (!window.Auth.isAuthenticated()) {
        window.location.href = 'login.html';
        return;
    }

    const urlParams = new URLSearchParams(window.location.search);
    const orderNumber = urlParams.get('orderNumber');
    const orderId = urlParams.get('id');

    if (orderNumber) {
        await loadOrderByNumber(orderNumber);
    } else if (orderId) {
        await loadOrderById(orderId);
    } else {
        window.location.href = 'account.html?tab=orders';
    }
});

async function loadOrderByNumber(orderNumber) {
    try {
        const res = await window.api.get(`/api/orders/track/${orderNumber}`);
        if (res.success && res.data) {
            currentOrder = res.data;
            renderOrderTracking(currentOrder);
        }
    } catch (e) {
        renderTrackingError(e.message);
    }
}

async function loadOrderById(orderId) {
    try {
        const res = await window.api.get(`/api/orders/${orderId}`);
        if (res.success && res.data) {
            currentOrder = res.data;
            renderOrderTracking(currentOrder);
        }
    } catch (e) {
        renderTrackingError(e.message);
    }
}

function renderOrderTracking(order) {
    document.getElementById('track-order-num').textContent = order.orderNumber;
    document.getElementById('track-order-date').textContent = window.UI.formatDate(order.createdAt);
    document.getElementById('track-order-status-badge').textContent = order.orderStatus;
    document.getElementById('track-order-status-badge').className = `status-pill ${order.orderStatus.toLowerCase()}`;

    // Update Progress Stepper
    updateTrackingTimeline(order.orderStatus);

    // Shipping & Payment Info
    document.getElementById('track-shipping-address').textContent = order.shippingAddress;
    document.getElementById('track-payment-method').textContent = `${order.paymentMethod === 'COD' ? 'Cash on Delivery' : 'Online Payment (Mock)'} [${order.paymentStatus}]`;

    // Order Items
    const itemsContainer = document.getElementById('track-order-items');
    itemsContainer.innerHTML = order.items.map(i => `
        <div style="display: flex; align-items: center; gap: 1rem; padding: 0.85rem 0; border-bottom: 1px solid var(--border-color);">
            <img src="${i.productImage || 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=200'}" alt="${i.productName}" style="width: 60px; height: 60px; object-fit: cover; border-radius: var(--radius-sm);">
            <div style="flex: 1;">
                <h4 style="font-size: 0.95rem; color: var(--text-primary);"><a href="product-detail.html?id=${i.productId}">${i.productName}</a></h4>
                <div style="font-size: 0.8rem; color: var(--text-muted); margin-top: 0.2rem;">Qty: ${i.quantity} × ${window.UI.formatCurrency(i.price)}</div>
            </div>
            <div style="font-weight: 700; color: var(--text-primary); font-size: 1rem;">
                ${window.UI.formatCurrency(i.itemTotal)}
            </div>
        </div>
    `).join('');

    // Price Breakdown
    document.getElementById('track-subtotal').textContent = window.UI.formatCurrency(order.totalAmount);
    document.getElementById('track-discount').textContent = `-${window.UI.formatCurrency(order.discount)}`;
    document.getElementById('track-delivery').textContent = order.deliveryCharge === 0 ? 'FREE' : window.UI.formatCurrency(order.deliveryCharge);
    document.getElementById('track-final-total').textContent = window.UI.formatCurrency(order.finalAmount);

    // Cancel Button
    const cancelBtn = document.getElementById('cancel-order-btn');
    if (order.cancellable) {
        cancelBtn.style.display = 'inline-block';
        cancelBtn.onclick = () => cancelOrder(order.id);
    } else {
        cancelBtn.style.display = 'none';
    }
}

function updateTrackingTimeline(status) {
    const steps = ['PLACED', 'CONFIRMED', 'PACKED', 'SHIPPED', 'OUT_FOR_DELIVERY', 'DELIVERED'];
    const currentIdx = steps.indexOf(status);

    if (status === 'CANCELLED') {
        document.getElementById('tracking-stepper-wrap').innerHTML = `
            <div style="background: #FEE2E2; border: 1px solid #EF4444; border-radius: var(--radius-md); padding: 1.25rem; text-align: center; color: #991B1B; font-weight: 700;">
                ❌ This order has been CANCELLED. Any processed payments are being refunded.
            </div>
        `;
        return;
    }

    steps.forEach((step, idx) => {
        const stepEl = document.getElementById(`step-${step.toLowerCase()}`);
        if (!stepEl) return;

        if (idx <= currentIdx) {
            stepEl.classList.add('completed');
            if (idx === currentIdx) {
                stepEl.classList.add('active');
            }
        } else {
            stepEl.classList.remove('completed', 'active');
        }
    });
}

async function cancelOrder(orderId) {
    if (!confirm('Are you sure you want to cancel this order?')) return;

    try {
        const res = await window.api.post(`/api/orders/${orderId}/cancel`);
        if (res.success) {
            window.UI.showToast('Order cancelled successfully', 'info');
            currentOrder = res.data;
            renderOrderTracking(currentOrder);
        }
    } catch (e) {
        window.UI.showToast(e.message || 'Unable to cancel order', 'error');
    }
}

function renderTrackingError(msg) {
    document.getElementById('tracking-main-content').innerHTML = `
        <div style="text-align: center; padding: 4rem;">
            <h2>Order Not Found</h2>
            <p style="color: var(--text-muted); margin-top: 0.5rem;">${msg || 'Could not locate order with the specified details.'}</p>
            <a href="account.html?tab=orders" class="btn-hero" style="margin-top: 1.5rem;">View All Orders</a>
        </div>
    `;
}
