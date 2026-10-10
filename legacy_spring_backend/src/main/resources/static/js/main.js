/**
 * HariMart Main UI and Application Utilities
 * Shared helper functions across all HariMart storefront pages.
 */

const UI = {
    showToast(message, type = 'success', duration = 3500) {
        let container = document.getElementById('toast-container');
        if (!container) {
            container = document.createElement('div');
            container.id = 'toast-container';
            document.body.appendChild(container);
        }

        const toast = document.createElement('div');
        toast.className = `toast ${type}`;

        const icon = type === 'success' ? '✓' : type === 'error' ? '✕' : 'ℹ';
        toast.innerHTML = `<span style="font-weight: bold; font-size: 1.1rem;">${icon}</span><span>${message}</span>`;

        container.appendChild(toast);

        setTimeout(() => {
            toast.style.opacity = '0';
            toast.style.transform = 'translateX(100%)';
            toast.style.transition = 'all 0.3s ease';
            setTimeout(() => toast.remove(), 300);
        }, duration);
    },

    formatCurrency(amount) {
        if (amount === null || amount === undefined) return '₹0.00';
        const num = parseFloat(amount);
        return '₹' + num.toLocaleString('en-IN', {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        });
    },

    formatDate(dateString) {
        if (!dateString) return 'N/A';
        const d = new Date(dateString);
        return d.toLocaleDateString('en-IN', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    },

    renderStarRating(rating = 0) {
        const fullStars = Math.floor(rating);
        const hasHalfStar = rating - fullStars >= 0.4;
        let stars = '';

        for (let i = 0; i < fullStars; i++) {
            stars += '★';
        }
        if (hasHalfStar) {
            stars += '½';
        }
        while (stars.length < 5) {
            stars += '☆';
        }
        return stars;
    },

    openModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.add('active');
            document.body.style.overflow = 'hidden';
        }
    },

    closeModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.remove('active');
            document.body.style.overflow = '';
        }
    },

    setupModalDismissal() {
        document.querySelectorAll('.modal-overlay').forEach(overlay => {
            overlay.addEventListener('click', (e) => {
                if (e.target === overlay) {
                    overlay.classList.remove('active');
                    document.body.style.overflow = '';
                }
            });
            const closeBtn = overlay.querySelector('.modal-close-btn');
            if (closeBtn) {
                closeBtn.addEventListener('click', () => {
                    overlay.classList.remove('active');
                    document.body.style.overflow = '';
                });
            }
        });
    }
};

window.UI = UI;

document.addEventListener('DOMContentLoaded', () => {
    UI.setupModalDismissal();

    // Auto-populate search categories if dropdown is present
    const searchCatSelect = document.getElementById('search-category-select');
    if (searchCatSelect && searchCatSelect.options.length <= 1 && window.api) {
        window.api.get('/categories')
            .then(res => {
                if (res.data) {
                    res.data.forEach(cat => {
                        const opt = document.createElement('option');
                        opt.value = cat.name;
                        opt.textContent = cat.name;
                        searchCatSelect.appendChild(opt);
                    });
                }
            })
            .catch(() => {});
    }

    // Refresh cart badge if user is logged in
    if (window.Cart && window.Auth && window.Auth.isLoggedIn()) {
        window.Cart.updateBadge();
    }
});
