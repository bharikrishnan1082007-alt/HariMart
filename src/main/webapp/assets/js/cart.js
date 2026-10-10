/**
 * HariMart Shopping Cart AJAX Handlers
 */

async function updateCartItemQuantity(cartItemId, quantity, contextPath) {
    try {
        const response = await fetch(`${contextPath}/cart/update`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'X-Requested-With': 'XMLHttpRequest'
            },
            body: `cartItemId=${cartItemId}&quantity=${quantity}`
        });

        const data = await response.json();
        if (data.success) {
            // Update summary UI if elements exist
            const subtotalEl = document.getElementById('cartSubtotal');
            const discountEl = document.getElementById('cartDiscount');
            const taxEl = document.getElementById('cartTax');
            const shippingEl = document.getElementById('cartShipping');
            const totalEl = document.getElementById('cartTotal');
            const badge = document.getElementById('cartCountBadge');

            if (subtotalEl) subtotalEl.textContent = data.subtotal;
            if (discountEl) discountEl.textContent = data.discount;
            if (taxEl) taxEl.textContent = data.tax;
            if (shippingEl) shippingEl.textContent = data.shipping;
            if (totalEl) totalEl.textContent = data.total;
            if (badge) {
                badge.textContent = data.cartCount;
                badge.style.display = data.cartCount > 0 ? 'inline-block' : 'none';
            }

            if (quantity <= 0) {
                const row = document.getElementById(`cartRow-${cartItemId}`);
                if (row) row.remove();
                if (data.cartCount === 0) {
                    window.location.reload();
                }
            } else {
                const qtyInput = document.getElementById(`cartQtyInput-${cartItemId}`);
                if (qtyInput) qtyInput.value = quantity;
            }

            showToast(data.message, 'success');
        } else {
            showToast(data.message, 'error');
        }
    } catch (e) {
        console.error('Cart update error:', e);
    }
}

async function addToCartAjax(productId, quantity, contextPath) {
    try {
        const response = await fetch(`${contextPath}/cart/add`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'X-Requested-With': 'XMLHttpRequest'
            },
            body: `productId=${productId}&quantity=${quantity}`
        });

        const data = await response.json();
        if (data.success) {
            const badge = document.getElementById('cartCountBadge');
            if (badge) {
                badge.textContent = data.cartCount;
                badge.style.display = 'inline-block';
            }
            showToast(data.message, 'success');
        } else {
            showToast(data.message, 'error');
        }
    } catch (e) {
        console.error('Add to cart error:', e);
    }
}
