/**
 * HariMart Storefront Main JavaScript
 */

function showToast(message, type = 'success') {
    let container = document.getElementById('toastContainer');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toastContainer';
        container.className = 'toast-container';
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerHTML = `<span>${type === 'success' ? '✓' : '✕'}</span> <span>${message}</span>`;
    container.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(100%)';
        toast.style.transition = 'all 0.3s ease';
        setTimeout(() => toast.remove(), 300);
    }, 3500);
}

// Global Wishlist Toggle Handler
document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.wishlist-toggle-btn').forEach(btn => {
        btn.addEventListener('click', async (e) => {
            e.preventDefault();
            e.stopPropagation();

            const productId = btn.dataset.productId;
            const contextPath = btn.dataset.contextPath || '';

            try {
                const response = await fetch(`${contextPath}/wishlist/toggle`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                        'X-Requested-With': 'XMLHttpRequest'
                    },
                    body: `productId=${productId}`
                });

                const data = await response.json();
                if (data.requireLogin) {
                    window.location.href = `${contextPath}/login?returnUrl=${encodeURIComponent(window.location.pathname)}`;
                    return;
                }

                if (data.success) {
                    btn.classList.toggle('active', data.inWishlist);
                    showToast(data.message, 'success');
                    // Update Wishlist badge counter if present
                    const badge = document.getElementById('wishlistCountBadge');
                    if (badge) {
                        badge.textContent = data.count;
                        badge.style.display = data.count > 0 ? 'inline-block' : 'none';
                    }
                } else {
                    showToast(data.message, 'error');
                }
            } catch (err) {
                console.error('Wishlist error:', err);
            }
        });
    });
});
