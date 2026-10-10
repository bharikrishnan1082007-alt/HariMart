/**
 * HariMart Product Detail Controller
 */

let currentProduct = null;
let selectedQuantity = 1;

document.addEventListener('DOMContentLoaded', async () => {
    const urlParams = new URLSearchParams(window.location.search);
    const productId = urlParams.get('id');

    if (!productId) {
        window.location.href = 'products.html';
        return;
    }

    await loadProductDetails(productId);
    await loadProductReviews(productId);
    await loadRelatedProducts(productId);
    setupDetailActions();
});

async function loadProductDetails(productId) {
    try {
        const res = await window.api.get(`/api/products/${productId}`);
        if (res.success && res.data) {
            currentProduct = res.data;
            renderProductDetails(currentProduct);
        } else {
            throw new Error('Product not found');
        }
    } catch (e) {
        document.getElementById('product-detail-container').innerHTML = `
            <div style="text-align: center; padding: 4rem;">
                <h2>Product Not Found</h2>
                <p style="color: var(--text-muted); margin-top: 0.5rem;">The product you are looking for does not exist or has been removed.</p>
                <a href="products.html" class="btn-hero" style="margin-top: 1.5rem;">Browse All Products</a>
            </div>
        `;
    }
}

function renderProductDetails(p) {
    document.title = `${p.name} | HariMart`;
    document.getElementById('breadcrumb-title').textContent = p.name;
    document.getElementById('breadcrumb-category').textContent = p.categoryName || 'Products';
    document.getElementById('breadcrumb-category').href = `products.html?category=${encodeURIComponent(p.categoryName || '')}`;

    // Images Gallery
    const mainImg = document.getElementById('main-gallery-image');
    const thumbsContainer = document.getElementById('gallery-thumbnails');

    mainImg.src = p.imageUrl || 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800';
    mainImg.alt = p.name;

    const allImages = p.images && p.images.length > 0 ? p.images : [p.imageUrl];
    thumbsContainer.innerHTML = allImages.map((img, idx) => `
        <div class="thumb-item ${idx === 0 ? 'active' : ''}" onclick="switchMainImage('${img}', this)">
            <img src="${img}" alt="${p.name} thumb ${idx + 1}" loading="lazy">
        </div>
    `).join('');

    // Product Title & Brand
    document.getElementById('detail-brand').textContent = p.brand;
    document.getElementById('detail-title').textContent = p.name;

    // Rating & Reviews
    document.getElementById('detail-rating-num').textContent = p.rating ? p.rating.toFixed(1) : '4.5';
    document.getElementById('detail-stars').textContent = window.UI.renderStarRating(p.rating || 4.5);
    document.getElementById('detail-review-count').textContent = `(${p.reviewCount || 0} reviews)`;

    // Pricing
    document.getElementById('detail-final-price').textContent = window.UI.formatCurrency(p.finalPrice || p.price);
    if (p.discount && p.discount > 0) {
        document.getElementById('detail-original-price').textContent = window.UI.formatCurrency(p.price);
        document.getElementById('detail-discount-pill').textContent = `${Math.round(p.discount)}% OFF`;
        document.getElementById('detail-discount-pill').style.display = 'inline-block';
    } else {
        document.getElementById('detail-original-price').style.display = 'none';
        document.getElementById('detail-discount-pill').style.display = 'none';
    }

    // Stock Status
    const stockEl = document.getElementById('detail-stock-status');
    if (p.stock > 10) {
        stockEl.innerHTML = `<span class="stock-indicator stock-in">✔ In Stock (${p.stock} units available)</span>`;
    } else if (p.stock > 0) {
        stockEl.innerHTML = `<span class="stock-indicator stock-low">⚠️ Hurry! Only ${p.stock} left in stock</span>`;
    } else {
        stockEl.innerHTML = `<span class="stock-indicator stock-low">❌ Currently Out of Stock</span>`;
        document.getElementById('detail-add-cart-btn').disabled = true;
        document.getElementById('detail-buy-now-btn').disabled = true;
    }

    // Description
    document.getElementById('detail-description').textContent = p.description || 'No detailed description available.';
}

function switchMainImage(imgUrl, thumbEl) {
    const mainImg = document.getElementById('main-gallery-image');
    mainImg.src = imgUrl;

    document.querySelectorAll('.thumb-item').forEach(t => t.classList.remove('active'));
    thumbEl.classList.add('active');
}

function setupDetailActions() {
    // Quantity buttons
    const qtyInput = document.getElementById('detail-qty-input');
    const decBtn = document.getElementById('qty-dec-btn');
    const incBtn = document.getElementById('qty-inc-btn');

    if (decBtn && incBtn && qtyInput) {
        decBtn.addEventListener('click', () => {
            let val = parseInt(qtyInput.value) || 1;
            if (val > 1) {
                val--;
                qtyInput.value = val;
                selectedQuantity = val;
            }
        });

        incBtn.addEventListener('click', () => {
            let val = parseInt(qtyInput.value) || 1;
            const max = currentProduct ? currentProduct.stock : 99;
            if (val < max) {
                val++;
                qtyInput.value = val;
                selectedQuantity = val;
            } else {
                window.UI.showToast(`Maximum available stock is ${max}`, 'warning');
            }
        });
    }

    // Add to Cart button
    const addCartBtn = document.getElementById('detail-add-cart-btn');
    if (addCartBtn) {
        addCartBtn.addEventListener('click', () => {
            if (currentProduct) {
                window.Cart.addToCart(currentProduct.id, selectedQuantity);
            }
        });
    }

    // Buy Now button
    const buyNowBtn = document.getElementById('detail-buy-now-btn');
    if (buyNowBtn) {
        buyNowBtn.addEventListener('click', () => {
            if (currentProduct) {
                window.Cart.buyNow(currentProduct.id, selectedQuantity);
            }
        });
    }

    // Pincode Delivery Estimator
    const pinCheckBtn = document.getElementById('pincode-check-btn');
    const pinInput = document.getElementById('pincode-input');
    const pinResult = document.getElementById('pincode-result');

    if (pinCheckBtn && pinInput && pinResult) {
        pinCheckBtn.addEventListener('click', () => {
            const pin = pinInput.value.trim();
            if (/^[1-9][0-9]{5}$/.test(pin)) {
                pinResult.innerHTML = `
                    <div style="color: var(--primary); font-weight: 600; margin-top: 0.5rem; font-size: 0.85rem;">
                        ✔ Delivery available to <strong>${pin}</strong> by <strong>Tomorrow, 4 PM</strong>! Free Delivery eligible.
                    </div>
                `;
            } else {
                pinResult.innerHTML = `
                    <div style="color: var(--accent-crimson); font-weight: 600; margin-top: 0.5rem; font-size: 0.85rem;">
                        Please enter a valid 6-digit Indian PIN code.
                    </div>
                `;
            }
        });
    }

    // Review Form Submission
    const reviewForm = document.getElementById('submit-review-form');
    if (reviewForm) {
        reviewForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            if (!window.Auth.isAuthenticated()) {
                window.UI.showToast('Please sign in to post your review', 'info');
                return;
            }

            const rating = parseInt(document.getElementById('review-rating-select').value);
            const comment = document.getElementById('review-comment-input').value.trim();

            try {
                const res = await window.api.post(`/api/products/${currentProduct.id}/reviews`, { rating, comment });
                if (res.success) {
                    window.UI.showToast('Review posted successfully! Thank you.', 'success');
                    reviewForm.reset();
                    await loadProductReviews(currentProduct.id);
                }
            } catch (err) {
                window.UI.showToast(err.message || 'Failed to submit review', 'error');
            }
        });
    }
}

async function loadProductReviews(productId) {
    const listEl = document.getElementById('reviews-list');
    if (!listEl) return;

    try {
        const res = await window.api.get(`/api/products/${productId}/reviews`);
        if (res.success && res.data) {
            const reviews = res.data;
            if (reviews.length === 0) {
                listEl.innerHTML = `<p style="color: var(--text-muted); font-style: italic; padding: 1rem 0;">No reviews yet. Be the first to review this product!</p>`;
                return;
            }

            listEl.innerHTML = reviews.map(r => `
                <div class="review-item" style="border-bottom: 1px solid var(--border-color); padding: 1rem 0;">
                    <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 0.35rem;">
                        <span style="font-weight: 700; color: var(--text-primary);">${r.userName || 'Customer'}</span>
                        <span style="font-size: 0.775rem; color: var(--text-muted);">${window.UI.formatDate(r.createdAt)}</span>
                    </div>
                    <div style="color: var(--accent-amber); font-size: 0.95rem; margin-bottom: 0.4rem;">
                        ${window.UI.renderStarRating(r.rating)}
                    </div>
                    <p style="color: var(--text-secondary); font-size: 0.9rem; line-height: 1.4;">${r.comment || 'No written comment.'}</p>
                </div>
            `).join('');
        }
    } catch (e) {
        listEl.innerHTML = `<p style="color: var(--text-muted);">Unable to load reviews.</p>`;
    }
}

async function loadRelatedProducts(productId) {
    const container = document.getElementById('related-products-grid');
    if (!container) return;

    try {
        const res = await window.api.get(`/api/products/${productId}/related?limit=4`);
        if (res.success && res.data && res.data.length > 0) {
            container.innerHTML = res.data.map(p => createProductCardHtml(p)).join('');
        } else {
            document.getElementById('related-section-wrap').style.display = 'none';
        }
    } catch (e) {
        console.warn(e);
    }
}
