/**
 * HariMart Admin Portal Controller
 */

let allAdminProducts = [];
let allAdminOrders = [];
let allAdminCategories = [];
let allAdminUsers = [];
let allAdminReviews = [];

document.addEventListener('DOMContentLoaded', async () => {
    // Check Admin Authentication
    if (!window.Auth.isAuthenticated() || !window.Auth.isAdmin()) {
        window.UI.showToast('Access denied. Administrator privileges required.', 'error');
        setTimeout(() => {
            window.location.href = 'login.html?redirect=admin.html';
        }, 1200);
        return;
    }

    setupAdminNavigation();
    await loadDashboardStats();
    await loadAdminProducts();
    await loadAdminCategories();
    await loadAdminOrders();
    await loadAdminUsers();
    await loadAdminReviews();
    setupProductModal();
    setupCategoryModal();
});

function setupAdminNavigation() {
    const navLinks = document.querySelectorAll('.admin-nav-link');
    const panes = document.querySelectorAll('.admin-tab-pane');
    const titleEl = document.getElementById('admin-topbar-title');

    navLinks.forEach(link => {
        link.addEventListener('click', () => {
            const target = link.getAttribute('data-pane');
            if (!target) return;

            navLinks.forEach(l => l.classList.remove('active'));
            panes.forEach(p => p.classList.remove('active'));

            link.classList.add('active');
            const targetPane = document.getElementById(`pane-${target}`);
            if (targetPane) targetPane.classList.add('active');

            if (titleEl) {
                titleEl.textContent = link.querySelector('span') ? link.querySelector('span').textContent : 'Dashboard';
            }
        });
    });
}

// 1. Dashboard Overview
async function loadDashboardStats() {
    try {
        const res = await window.api.get('/api/admin/dashboard');
        if (res.success && res.data) {
            const stats = res.data;
            document.getElementById('stat-total-revenue').textContent = window.UI.formatCurrency(stats.totalRevenue);
            document.getElementById('stat-total-orders').textContent = stats.totalOrders;
            document.getElementById('stat-pending-orders').textContent = stats.pendingOrders;
            document.getElementById('stat-total-products').textContent = stats.totalProducts;
            document.getElementById('stat-total-users').textContent = stats.totalUsers;
            document.getElementById('stat-low-stock').textContent = stats.lowStockProducts;

            // Render Recent Orders
            const recentOrdersTable = document.getElementById('recent-orders-table-body');
            if (recentOrdersTable && stats.recentOrders) {
                recentOrdersTable.innerHTML = stats.recentOrders.map(o => `
                    <tr>
                        <td><strong>${o.orderNumber}</strong></td>
                        <td>${o.customerName || 'Customer'}</td>
                        <td>${window.UI.formatDate(o.createdAt)}</td>
                        <td><span class="status-pill ${o.orderStatus.toLowerCase()}">${o.orderStatus}</span></td>
                        <td><strong>${window.UI.formatCurrency(o.finalAmount)}</strong></td>
                        <td>
                            <button class="btn-card-cart" style="padding: 0.3rem 0.6rem; font-size: 0.75rem;" onclick="viewOrderDetailsModal('${o.orderNumber}')">View</button>
                        </td>
                    </tr>
                `).join('');
            }
        }
    } catch (e) {
        console.warn('Dashboard stats error', e);
    }
}

// 2. Product Management
async function loadAdminProducts() {
    try {
        const res = await window.api.get('/api/products?size=100&sort=newest');
        if (res.success && res.data) {
            allAdminProducts = res.data.content || [];
            renderProductsTable(allAdminProducts);
        }
    } catch (e) {
        console.warn(e);
    }
}

function renderProductsTable(products) {
    const tbody = document.getElementById('admin-products-table-body');
    if (!tbody) return;

    if (products.length === 0) {
        tbody.innerHTML = `<tr><td colspan="7" style="text-align: center; color: var(--text-muted); padding: 2rem;">No products found</td></tr>`;
        return;
    }

    tbody.innerHTML = products.map(p => `
        <tr>
            <td>
                <div style="display: flex; align-items: center; gap: 0.75rem;">
                    <img src="${p.imageUrl || 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=100'}" alt="${p.name}" style="width: 44px; height: 44px; object-fit: cover; border-radius: var(--radius-sm);">
                    <div>
                        <strong style="color: var(--text-primary); font-size: 0.9rem;">${p.name}</strong>
                        <div style="font-size: 0.75rem; color: var(--text-muted);">${p.brand}</div>
                    </div>
                </div>
            </td>
            <td>${p.categoryName || 'General'}</td>
            <td><strong>${window.UI.formatCurrency(p.price)}</strong></td>
            <td>${p.discount ? `${p.discount}%` : '0%'}</td>
            <td>
                <span class="${p.stock <= 5 ? 'status-pill inactive' : 'status-pill active'}">
                    ${p.stock} units
                </span>
            </td>
            <td><span class="status-pill ${p.status.toLowerCase()}">${p.status}</span></td>
            <td>
                <div class="table-actions">
                    <button class="btn-icon" title="Edit Product" onclick="openEditProductModal(${p.id})">✏️</button>
                    <button class="btn-icon delete" title="Delete Product" onclick="deleteProduct(${p.id})">🗑️</button>
                </div>
            </td>
        </tr>
    `).join('');
}

function setupProductModal() {
    const addBtn = document.getElementById('btn-add-product-modal');
    const form = document.getElementById('product-modal-form');

    if (addBtn) {
        addBtn.addEventListener('click', () => {
            form.reset();
            document.getElementById('product-modal-id').value = '';
            document.getElementById('product-modal-title').textContent = 'Add New Product';
            populateCategorySelect();
            window.UI.openModal('product-modal');
        });
    }

    if (form) {
        form.onsubmit = async (e) => {
            e.preventDefault();
            const id = document.getElementById('product-modal-id').value;
            const name = document.getElementById('prod-name').value.trim();
            const categoryId = parseInt(document.getElementById('prod-category').value);
            const brand = document.getElementById('prod-brand').value.trim();
            const price = parseFloat(document.getElementById('prod-price').value);
            const discount = parseFloat(document.getElementById('prod-discount').value) || 0;
            const stock = parseInt(document.getElementById('prod-stock').value) || 0;
            const imageUrl = document.getElementById('prod-image').value.trim();
            const extraImagesStr = document.getElementById('prod-extra-images').value.trim();
            const description = document.getElementById('prod-desc').value.trim();

            const extraImages = extraImagesStr ? extraImagesStr.split('\n').map(s => s.trim()).filter(Boolean) : [];

            const payload = {
                name, categoryId, brand, price, discount, stock, imageUrl, description, images: extraImages, status: 'ACTIVE'
            };

            try {
                if (id) {
                    await window.api.put(`/api/products/${id}`, payload);
                    window.UI.showToast('Product updated successfully!', 'success');
                } else {
                    await window.api.post('/api/products', payload);
                    window.UI.showToast('Product added successfully!', 'success');
                }
                window.UI.closeModal('product-modal');
                await loadAdminProducts();
                await loadDashboardStats();
            } catch (err) {
                window.UI.showToast(err.message || 'Product save failed', 'error');
            }
        };
    }

    // Search filter
    const searchInput = document.getElementById('admin-product-search');
    if (searchInput) {
        searchInput.addEventListener('input', (e) => {
            const term = e.target.value.toLowerCase();
            const filtered = allAdminProducts.filter(p => 
                p.name.toLowerCase().includes(term) || 
                p.brand.toLowerCase().includes(term) ||
                (p.categoryName && p.categoryName.toLowerCase().includes(term))
            );
            renderProductsTable(filtered);
        });
    }
}

function openEditProductModal(productId) {
    const p = allAdminProducts.find(item => item.id === productId);
    if (!p) return;

    populateCategorySelect();
    document.getElementById('product-modal-id').value = p.id;
    document.getElementById('product-modal-title').textContent = 'Edit Product';
    document.getElementById('prod-name').value = p.name;
    document.getElementById('prod-category').value = p.categoryId;
    document.getElementById('prod-brand').value = p.brand;
    document.getElementById('prod-price').value = p.price;
    document.getElementById('prod-discount').value = p.discount || 0;
    document.getElementById('prod-stock').value = p.stock;
    document.getElementById('prod-image').value = p.imageUrl || '';
    document.getElementById('prod-extra-images').value = p.images ? p.images.join('\n') : '';
    document.getElementById('prod-desc').value = p.description || '';

    window.UI.openModal('product-modal');
}

async function deleteProduct(productId) {
    if (!confirm('Are you sure you want to deactivate/delete this product?')) return;
    try {
        await window.api.delete(`/api/products/${productId}`);
        window.UI.showToast('Product deactivated', 'info');
        await loadAdminProducts();
        await loadDashboardStats();
    } catch (e) {
        window.UI.showToast(e.message || 'Delete failed', 'error');
    }
}

function populateCategorySelect() {
    const sel = document.getElementById('prod-category');
    if (!sel) return;
    sel.innerHTML = allAdminCategories.map(c => `<option value="${c.id}">${c.name}</option>`).join('');
}

// 3. Category Management
async function loadAdminCategories() {
    try {
        const res = await window.api.get('/api/categories');
        if (res.success && res.data) {
            allAdminCategories = res.data;
            renderCategoriesTable(allAdminCategories);
        }
    } catch (e) {
        console.warn(e);
    }
}

function renderCategoriesTable(categories) {
    const tbody = document.getElementById('admin-categories-table-body');
    if (!tbody) return;

    tbody.innerHTML = categories.map(c => `
        <tr>
            <td>
                <div style="display:flex; align-items:center; gap:0.75rem;">
                    <img src="${c.image || 'https://images.unsplash.com/photo-1542838132-92c53300491e?w=100'}" alt="${c.name}" style="width:40px; height:40px; border-radius:var(--radius-sm); object-fit:cover;">
                    <strong style="color:var(--text-primary); font-size:0.95rem;">${c.name}</strong>
                </div>
            </td>
            <td>${c.description || 'No description'}</td>
            <td><span class="status-pill active">${c.productCount || 0} products</span></td>
            <td><span class="status-pill ${c.status.toLowerCase()}">${c.status}</span></td>
            <td>
                <div class="table-actions">
                    <button class="btn-icon" title="Edit Category" onclick="openEditCategoryModal(${c.id})">✏️</button>
                    <button class="btn-icon delete" title="Delete Category" onclick="deleteCategory(${c.id})">🗑️</button>
                </div>
            </td>
        </tr>
    `).join('');
}

function setupCategoryModal() {
    const addBtn = document.getElementById('btn-add-category-modal');
    const form = document.getElementById('category-modal-form');

    if (addBtn) {
        addBtn.addEventListener('click', () => {
            form.reset();
            document.getElementById('cat-modal-id').value = '';
            document.getElementById('cat-modal-title').textContent = 'Add Category';
            window.UI.openModal('category-modal');
        });
    }

    if (form) {
        form.onsubmit = async (e) => {
            e.preventDefault();
            const id = document.getElementById('cat-modal-id').value;
            const name = document.getElementById('cat-name').value.trim();
            const description = document.getElementById('cat-desc').value.trim();
            const image = document.getElementById('cat-image').value.trim();

            const payload = { name, description, image, status: 'ACTIVE' };

            try {
                if (id) {
                    await window.api.put(`/api/categories/${id}`, payload);
                    window.UI.showToast('Category updated!', 'success');
                } else {
                    await window.api.post('/api/categories', payload);
                    window.UI.showToast('Category created!', 'success');
                }
                window.UI.closeModal('category-modal');
                await loadAdminCategories();
            } catch (err) {
                window.UI.showToast(err.message || 'Category save failed', 'error');
            }
        };
    }
}

function openEditCategoryModal(catId) {
    const c = allAdminCategories.find(item => item.id === catId);
    if (!c) return;

    document.getElementById('cat-modal-id').value = c.id;
    document.getElementById('cat-modal-title').textContent = 'Edit Category';
    document.getElementById('cat-name').value = c.name;
    document.getElementById('cat-desc').value = c.description || '';
    document.getElementById('cat-image').value = c.image || '';

    window.UI.openModal('category-modal');
}

async function deleteCategory(catId) {
    if (!confirm('Are you sure you want to delete this category?')) return;
    try {
        await window.api.delete(`/api/categories/${catId}`);
        window.UI.showToast('Category deleted', 'info');
        await loadAdminCategories();
    } catch (e) {
        window.UI.showToast(e.message || 'Delete failed', 'error');
    }
}

// 4. Order Management
async function loadAdminOrders() {
    try {
        const res = await window.api.get('/api/admin/orders');
        if (res.success && res.data) {
            allAdminOrders = res.data;
            renderOrdersTable(allAdminOrders);
        }
    } catch (e) {
        console.warn(e);
    }
}

function renderOrdersTable(orders) {
    const tbody = document.getElementById('admin-orders-table-body');
    if (!tbody) return;

    tbody.innerHTML = orders.map(o => `
        <tr>
            <td><strong>${o.orderNumber}</strong></td>
            <td>
                <div>${o.customerName}</div>
                <div style="font-size:0.75rem; color:var(--text-muted);">${o.customerPhone}</div>
            </td>
            <td>${window.UI.formatDate(o.createdAt)}</td>
            <td><strong>${window.UI.formatCurrency(o.finalAmount)}</strong></td>
            <td>
                <select class="form-select" style="padding:0.25rem 0.5rem; font-size:0.8rem;" onchange="updateOrderStatus(${o.id}, this.value)">
                    <option value="PLACED" ${o.orderStatus === 'PLACED' ? 'selected' : ''}>PLACED</option>
                    <option value="CONFIRMED" ${o.orderStatus === 'CONFIRMED' ? 'selected' : ''}>CONFIRMED</option>
                    <option value="PACKED" ${o.orderStatus === 'PACKED' ? 'selected' : ''}>PACKED</option>
                    <option value="SHIPPED" ${o.orderStatus === 'SHIPPED' ? 'selected' : ''}>SHIPPED</option>
                    <option value="OUT_FOR_DELIVERY" ${o.orderStatus === 'OUT_FOR_DELIVERY' ? 'selected' : ''}>OUT FOR DELIVERY</option>
                    <option value="DELIVERED" ${o.orderStatus === 'DELIVERED' ? 'selected' : ''}>DELIVERED</option>
                    <option value="CANCELLED" ${o.orderStatus === 'CANCELLED' ? 'selected' : ''}>CANCELLED</option>
                </select>
            </td>
            <td>${o.paymentMethod} [${o.paymentStatus}]</td>
            <td>
                <button class="btn-card-cart" style="padding:0.35rem 0.75rem; font-size:0.75rem;" onclick="viewOrderDetailsModal('${o.orderNumber}')">Receipt</button>
            </td>
        </tr>
    `).join('');
}

async function updateOrderStatus(orderId, newStatus) {
    try {
        await window.api.put(`/api/orders/${orderId}/status`, { orderStatus: newStatus });
        window.UI.showToast(`Order updated to ${newStatus}`, 'success');
        await loadAdminOrders();
        await loadDashboardStats();
    } catch (e) {
        window.UI.showToast(e.message || 'Failed to update order status', 'error');
    }
}

function viewOrderDetailsModal(orderNumber) {
    const o = allAdminOrders.find(item => item.orderNumber === orderNumber);
    if (!o) return;

    document.getElementById('receipt-order-num').textContent = o.orderNumber;
    document.getElementById('receipt-customer').textContent = `${o.customerName} (${o.customerEmail}, ${o.customerPhone})`;
    document.getElementById('receipt-address').textContent = o.shippingAddress;
    document.getElementById('receipt-payment').textContent = `${o.paymentMethod} [${o.paymentStatus}]`;

    const itemsEl = document.getElementById('receipt-items');
    itemsEl.innerHTML = o.items.map(i => `
        <div style="display:flex; justify-content:space-between; padding:0.4rem 0; border-bottom:1px solid var(--border-color); font-size:0.85rem;">
            <span>${i.productName} × ${i.quantity}</span>
            <strong>${window.UI.formatCurrency(i.itemTotal)}</strong>
        </div>
    `).join('');

    document.getElementById('receipt-total').textContent = window.UI.formatCurrency(o.finalAmount);
    window.UI.openModal('order-receipt-modal');
}

// 5. User Management
async function loadAdminUsers() {
    try {
        const res = await window.api.get('/api/users');
        if (res.success && res.data) {
            allAdminUsers = res.data;
            renderUsersTable(allAdminUsers);
        }
    } catch (e) {
        console.warn(e);
    }
}

function renderUsersTable(users) {
    const tbody = document.getElementById('admin-users-table-body');
    if (!tbody) return;

    tbody.innerHTML = users.map(u => `
        <tr>
            <td><strong>#${u.id}</strong></td>
            <td>${u.name}</td>
            <td>${u.email}</td>
            <td>${u.phone}</td>
            <td><span class="status-pill ${u.role === 'ROLE_ADMIN' ? 'active' : 'confirmed'}">${u.role}</span></td>
            <td><span class="status-pill ${u.status.toLowerCase()}">${u.status}</span></td>
            <td>
                ${u.role !== 'ROLE_ADMIN' ? `
                    <button class="btn-card-cart" style="padding:0.3rem 0.6rem; font-size:0.75rem;" onclick="toggleUserStatus(${u.id})">
                        ${u.status === 'ACTIVE' ? 'Deactivate' : 'Activate'}
                    </button>
                ` : '<span style="color:var(--text-muted); font-size:0.8rem;">Protected</span>'}
            </td>
        </tr>
    `).join('');
}

async function toggleUserStatus(userId) {
    try {
        const res = await window.api.put(`/api/users/${userId}/status`, {});
        if (res.success) {
            window.UI.showToast('User status updated', 'info');
            await loadAdminUsers();
        }
    } catch (e) {
        window.UI.showToast(e.message || 'Status update failed', 'error');
    }
}

// 6. Review Moderation
async function loadAdminReviews() {
    try {
        const res = await window.api.get('/api/reviews');
        if (res.success && res.data) {
            allAdminReviews = res.data;
            renderReviewsTable(allAdminReviews);
        }
    } catch (e) {
        console.warn(e);
    }
}

function renderReviewsTable(reviews) {
    const tbody = document.getElementById('admin-reviews-table-body');
    if (!tbody) return;

    if (reviews.length === 0) {
        tbody.innerHTML = `<tr><td colspan="6" style="text-align:center; color:var(--text-muted); padding:2rem;">No customer reviews submitted yet</td></tr>`;
        return;
    }

    tbody.innerHTML = reviews.map(r => `
        <tr>
            <td><strong>${r.productName || 'Product'}</strong></td>
            <td>${r.userName || 'Customer'}</td>
            <td><span style="color:var(--accent-amber); font-weight:700;">★ ${r.rating}</span></td>
            <td style="max-width:300px; font-size:0.85rem; color:var(--text-secondary);">${r.comment || 'No comment'}</td>
            <td>${window.UI.formatDate(r.createdAt)}</td>
            <td>
                <button class="btn-icon delete" title="Delete Review" onclick="deleteAdminReview(${r.id})">🗑️</button>
            </td>
        </tr>
    `).join('');
}

async function deleteAdminReview(reviewId) {
    if (!confirm('Are you sure you want to delete this customer review?')) return;
    try {
        await window.api.delete(`/api/reviews/${reviewId}`);
        window.UI.showToast('Review removed', 'info');
        await loadAdminReviews();
    } catch (e) {
        window.UI.showToast(e.message || 'Delete failed', 'error');
    }
}
