/**
 * HariMart Shopping Cart Manager
 */

const Cart = {
    async getCart(couponCode = null) {
        try {
            const params = couponCode ? { coupon: couponCode } : {};
            const res = await window.api.get('/api/cart', params);
            if (res.success && res.data) {
                this.updateCartBadges(res.data.totalItems || 0);
                return res.data;
            }
            return null;
        } catch (error) {
            console.warn('Could not fetch cart:', error.message);
            return null;
        }
    },

    async addToCart(productId, quantity = 1) {
        if (!window.Auth.isAuthenticated()) {
            window.UI.showToast('Please sign in to add items to your cart', 'info');
            setTimeout(() => {
                window.location.href = `login.html?redirect=${encodeURIComponent(window.location.pathname + window.location.search)}`;
            }, 1000);
            return;
        }

        try {
            const res = await window.api.post('/api/cart/items', { productId, quantity });
            if (res.success && res.data) {
                this.updateCartBadges(res.data.totalItems || 0);
                window.UI.showToast('Added to Cart! 🛒', 'success');
                return res.data;
            }
        } catch (error) {
            window.UI.showToast(error.message || 'Failed to add item to cart', 'error');
            throw error;
        }
    },

    async buyNow(productId, quantity = 1) {
        if (!window.Auth.isAuthenticated()) {
            window.UI.showToast('Please sign in to proceed with checkout', 'info');
            setTimeout(() => {
                window.location.href = `login.html?redirect=${encodeURIComponent(`checkout.html?direct=true&product=${productId}&qty=${quantity}`)}`;
            }, 1000);
            return;
        }

        // Redirect directly to checkout with direct buy item
        window.location.href = `checkout.html?direct=true&product=${productId}&qty=${quantity}`;
    },

    async updateQuantity(cartItemId, quantity) {
        try {
            const res = await window.api.put(`/api/cart/items/${cartItemId}`, { quantity });
            if (res.success && res.data) {
                this.updateCartBadges(res.data.totalItems || 0);
                return res.data;
            }
        } catch (error) {
            window.UI.showToast(error.message || 'Failed to update item quantity', 'error');
            throw error;
        }
    },

    async removeItem(cartItemId) {
        try {
            const res = await window.api.delete(`/api/cart/items/${cartItemId}`);
            if (res.success && res.data) {
                this.updateCartBadges(res.data.totalItems || 0);
                window.UI.showToast('Item removed from cart', 'info');
                return res.data;
            }
        } catch (error) {
            window.UI.showToast(error.message || 'Failed to remove item', 'error');
            throw error;
        }
    },

    async clearCart() {
        try {
            await window.api.delete('/api/cart');
            this.updateCartBadges(0);
            window.UI.showToast('Cart cleared', 'info');
        } catch (error) {
            window.UI.showToast(error.message || 'Failed to clear cart', 'error');
        }
    },

    updateCartBadges(count) {
        const badges = document.querySelectorAll('.cart-count-badge');
        badges.forEach(b => {
            b.textContent = count;
            b.style.display = count > 0 ? 'flex' : 'none';
        });
    },

    async init() {
        if (window.Auth.isAuthenticated()) {
            try {
                await this.getCart();
            } catch (e) {
                // Ignore error on init
            }
        }
    }
};

window.Cart = Cart;
document.addEventListener('DOMContentLoaded', () => {
    Cart.init();
});
