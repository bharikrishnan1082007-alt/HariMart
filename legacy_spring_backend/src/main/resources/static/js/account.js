/**
 * HariMart Customer Account Controller
 */

document.addEventListener('DOMContentLoaded', async () => {
    if (!window.Auth.isAuthenticated()) {
        window.location.href = 'login.html';
        return;
    }

    setupAccountTabs();
    await loadUserProfile();
    await loadUserOrders();
    await loadUserAddresses();
    await loadUserWishlist();
    setupAddressModal();
});

function setupAccountTabs() {
    const tabButtons = document.querySelectorAll('.account-tab-btn');
    const tabPanes = document.querySelectorAll('.account-tab-pane');

    function switchTab(tabId) {
        tabButtons.forEach(b => b.classList.remove('active'));
        tabPanes.forEach(p => p.style.display = 'none');

        const activeBtn = document.querySelector(`.account-tab-btn[data-tab="${tabId}"]`);
        const activePane = document.getElementById(`tab-pane-${tabId}`);

        if (activeBtn) activeBtn.classList.add('active');
        if (activePane) activePane.style.display = 'block';
    }

    tabButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            const tabId = btn.getAttribute('data-tab');
            switchTab(tabId);
        });
    });

    // Check URL parameter for active tab e.g. account.html?tab=orders
    const urlParams = new URLSearchParams(window.location.search);
    const initialTab = urlParams.get('tab') || 'profile';
    switchTab(initialTab);
}

async function loadUserProfile() {
    try {
        const res = await window.api.get('/api/auth/me');
        if (res.success && res.data) {
            const user = res.data;
            document.getElementById('profile-name-display').textContent = user.name;
            document.getElementById('profile-email-display').textContent = user.email;
            document.getElementById('profile-name-input').value = user.name;
            document.getElementById('profile-phone-input').value = user.phone;
            document.getElementById('profile-email-input').value = user.email;

            // Profile form submit
            const profileForm = document.getElementById('profile-edit-form');
            if (profileForm) {
                profileForm.onsubmit = async (e) => {
                    e.preventDefault();
                    const name = document.getElementById('profile-name-input').value.trim();
                    const phone = document.getElementById('profile-phone-input').value.trim();
                    try {
                        const updateRes = await window.api.put('/api/users/profile', { name, phone });
                        if (updateRes.success) {
                            window.UI.showToast('Profile updated successfully!', 'success');
                            window.Auth.updateAuthUI();
                            loadUserProfile();
                        }
                    } catch (err) {
                        window.UI.showToast(err.message || 'Update failed', 'error');
                    }
                };
            }
        }
    } catch (e) {
        console.warn(e);
    }
}

async function loadUserOrders() {
    const listEl = document.getElementById('account-orders-list');
    if (!listEl) return;

    try {
        const res = await window.api.get('/api/orders');
        if (res.success && res.data) {
            const orders = res.data;
            if (orders.length === 0) {
                listEl.innerHTML = `
                    <div style="text-align: center; padding: 3rem; background: #FFFFFF; border-radius: var(--radius-md); border: 1px solid var(--border-color);">
                        <span style="font-size: 2.5rem;">📦</span>
                        <h4 style="margin-top: 0.75rem;">No orders yet</h4>
                        <p style="color: var(--text-muted); font-size: 0.875rem;">Start shopping and check out our latest offers!</p>
                        <a href="products.html" class="btn-hero" style="margin-top: 1rem; font-size: 0.85rem; padding: 0.5rem 1.25rem;">Shop Now</a>
                    </div>
                `;
                return;
            }

            listEl.innerHTML = orders.map(o => `
                <div class="order-card" style="background: #FFFFFF; border: 1px solid var(--border-color); border-radius: var(--radius-md); padding: 1.25rem; margin-bottom: 1.25rem; box-shadow: var(--shadow-sm);">
                    <div style="display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 0.5rem; border-bottom: 1px solid var(--border-color); padding-bottom: 0.75rem; margin-bottom: 0.75rem;">
                        <div>
                            <span style="font-size: 0.8rem; color: var(--text-muted);">ORDER #</span>
                            <strong style="color: var(--secondary); font-size: 0.95rem;">${o.orderNumber}</strong>
                            <span style="color: var(--text-muted); font-size: 0.8rem; margin-left: 0.75rem;">${window.UI.formatDate(o.createdAt)}</span>
                        </div>
                        <div style="display: flex; align-items: center; gap: 0.75rem;">
                            <span class="status-pill ${o.orderStatus.toLowerCase()}">${o.orderStatus}</span>
                            <a href="order-tracking.html?orderNumber=${o.orderNumber}" class="btn-card-cart" style="text-decoration: none; padding: 0.4rem 0.85rem; font-size: 0.8rem;">Track Order</a>
                        </div>
                    </div>
                    <div style="display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 1rem;">
                        <div style="display: flex; gap: 0.5rem; overflow-x: auto;">
                            ${o.items.map(i => `
                                <img src="${i.productImage || 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=150'}" alt="${i.productName}" title="${i.productName}" style="width: 54px; height: 54px; object-fit: cover; border-radius: var(--radius-sm); border: 1px solid var(--border-color);">
                            `).join('')}
                        </div>
                        <div style="text-align: right;">
                            <div style="font-size: 0.8rem; color: var(--text-muted);">Total (${o.items.length} items)</div>
                            <strong style="font-size: 1.15rem; color: var(--secondary);">${window.UI.formatCurrency(o.finalAmount)}</strong>
                        </div>
                    </div>
                </div>
            `).join('');
        }
    } catch (e) {
        listEl.innerHTML = `<p style="color: var(--accent-crimson);">Failed to load orders.</p>`;
    }
}

async function loadUserAddresses() {
    const listEl = document.getElementById('account-addresses-list');
    if (!listEl) return;

    try {
        const res = await window.api.get('/api/users/addresses');
        if (res.success && res.data) {
            const addresses = res.data;
            if (addresses.length === 0) {
                listEl.innerHTML = `<p style="color: var(--text-muted); padding: 1.5rem 0;">No saved addresses yet.</p>`;
                return;
            }

            listEl.innerHTML = addresses.map(a => `
                <div style="background: #FFFFFF; border: 1px solid var(--border-color); border-radius: var(--radius-md); padding: 1.25rem; margin-bottom: 1rem; display: flex; justify-content: space-between; align-items: flex-start;">
                    <div>
                        <strong style="color: var(--secondary);">${a.addressLine}</strong>
                        <p style="color: var(--text-secondary); font-size: 0.875rem; margin-top: 0.25rem;">
                            ${a.city}, ${a.state} - <strong>${a.pincode}</strong> (${a.country})
                        </p>
                        ${a.isDefault ? '<span class="status-pill active" style="margin-top: 0.5rem;">Default Delivery Address</span>' : ''}
                    </div>
                    <button class="btn-icon delete" title="Delete Address" onclick="deleteAddress(${a.id})">🗑️</button>
                </div>
            `).join('');
        }
    } catch (e) {
        console.warn(e);
    }
}

function setupAddressModal() {
    const addBtn = document.getElementById('add-address-btn');
    const form = document.getElementById('new-address-modal-form');

    if (addBtn) {
        addBtn.addEventListener('click', () => window.UI.openModal('address-modal'));
    }

    if (form) {
        form.onsubmit = async (e) => {
            e.preventDefault();
            const addressLine = document.getElementById('modal-addr-line').value.trim();
            const city = document.getElementById('modal-addr-city').value.trim();
            const state = document.getElementById('modal-addr-state').value.trim();
            const pincode = document.getElementById('modal-addr-pincode').value.trim();
            const isDefault = document.getElementById('modal-addr-default').checked;

            try {
                const res = await window.api.post('/api/users/addresses', {
                    addressLine, city, state, pincode, country: 'India', isDefault
                });
                if (res.success) {
                    window.UI.showToast('Address added successfully!', 'success');
                    window.UI.closeModal('address-modal');
                    form.reset();
                    await loadUserAddresses();
                }
            } catch (err) {
                window.UI.showToast(err.message || 'Failed to save address', 'error');
            }
        };
    }
}

async function deleteAddress(addressId) {
    if (!confirm('Are you sure you want to delete this address?')) return;
    try {
        await window.api.delete(`/api/users/addresses/${addressId}`);
        window.UI.showToast('Address deleted', 'info');
        await loadUserAddresses();
    } catch (e) {
        window.UI.showToast(e.message || 'Failed to delete address', 'error');
    }
}

async function loadUserWishlist() {
    const grid = document.getElementById('account-wishlist-grid');
    if (!grid) return;

    try {
        const res = await window.api.get('/api/wishlist');
        if (res.success && res.data) {
            const items = res.data;
            if (items.length === 0) {
                grid.innerHTML = `
                    <div style="grid-column: 1/-1; text-align: center; padding: 3rem; background: #FFFFFF; border-radius: var(--radius-md); border: 1px solid var(--border-color);">
                        <span style="font-size: 2.5rem;">❤️</span>
                        <h4 style="margin-top: 0.75rem;">Your wishlist is empty</h4>
                        <p style="color: var(--text-muted); font-size: 0.875rem;">Browse items and click the heart icon to save favorites!</p>
                        <a href="products.html" class="btn-hero" style="margin-top: 1rem; font-size: 0.85rem; padding: 0.5rem 1.25rem;">Explore HariMart</a>
                    </div>
                `;
                return;
            }

            grid.innerHTML = items.map(p => `
                <div class="product-card">
                    <div class="product-image-container">
                        <a href="product-detail.html?id=${p.id}">
                            <img src="${p.imageUrl}" alt="${p.name}">
                        </a>
                        <button class="wishlist-heart-btn active" title="Remove from Wishlist" onclick="removeWishlistItem(${p.id})">
                            ✕
                        </button>
                    </div>
                    <div class="product-info">
                        <span class="product-brand">${p.brand}</span>
                        <a href="product-detail.html?id=${p.id}" class="product-title">${p.name}</a>
                        <div class="product-price-wrap">
                            <span class="final-price">${window.UI.formatCurrency(p.finalPrice || p.price)}</span>
                        </div>
                        <div class="card-actions" style="grid-template-columns: 1fr;">
                            <button class="btn-card-cart" onclick="window.Cart.addToCart(${p.id}, 1)">Move to Cart</button>
                        </div>
                    </div>
                </div>
            `).join('');
        }
    } catch (e) {
        grid.innerHTML = `<p style="color: var(--accent-crimson);">Failed to load wishlist.</p>`;
    }
}

async function removeWishlistItem(productId) {
    try {
        await window.api.delete(`/api/wishlist/${productId}`);
        window.UI.showToast('Removed from wishlist', 'info');
        await loadUserWishlist();
    } catch (e) {
        window.UI.showToast(e.message || 'Error', 'error');
    }
}
