/**
 * HariMart Home Page Controller
 */

document.addEventListener('DOMContentLoaded', async () => {
    initHeroSlider();
    initFlashCountdown();
    await loadCategories();
    await loadFeaturedProducts();
    await loadTrendingProducts();
    await loadBestSellers();
});

function initHeroSlider() {
    const slides = document.querySelectorAll('.hero-slide');
    if (!slides.length) return;

    let currentSlide = 0;
    const totalSlides = slides.length;

    function showSlide(index) {
        slides.forEach(s => s.classList.remove('active'));
        slides[index].classList.add('active');
    }

    const prevBtn = document.getElementById('hero-prev');
    const nextBtn = document.getElementById('hero-next');

    if (prevBtn) {
        prevBtn.addEventListener('click', () => {
            currentSlide = (currentSlide - 1 + totalSlides) % totalSlides;
            showSlide(currentSlide);
        });
    }

    if (nextBtn) {
        nextBtn.addEventListener('click', () => {
            currentSlide = (currentSlide + 1) % totalSlides;
            showSlide(currentSlide);
        });
    }

    // Auto rotate every 5 seconds
    setInterval(() => {
        currentSlide = (currentSlide + 1) % totalSlides;
        showSlide(currentSlide);
    }, 5000);
}

function initFlashCountdown() {
    const hoursEl = document.getElementById('flash-hrs');
    const minsEl = document.getElementById('flash-mins');
    const secsEl = document.getElementById('flash-secs');

    if (!hoursEl || !minsEl || !secsEl) return;

    // Countdown target: 6 hours from now
    let remainingSeconds = 6 * 3600 + 45 * 60 + 20;

    setInterval(() => {
        if (remainingSeconds > 0) {
            remainingSeconds--;
            const hrs = Math.floor(remainingSeconds / 3600);
            const mins = Math.floor((remainingSeconds % 3600) / 60);
            const secs = remainingSeconds % 60;

            hoursEl.textContent = String(hrs).padStart(2, '0');
            minsEl.textContent = String(mins).padStart(2, '0');
            secsEl.textContent = String(secs).padStart(2, '0');
        }
    }, 1000);
}

async function loadCategories() {
    const grid = document.getElementById('categories-grid');
    const catNavList = document.getElementById('cat-nav-list');
    const catSelect = document.getElementById('search-category-select');

    try {
        const res = await window.api.get('/api/categories');
        if (res.success && res.data) {
            const categories = res.data;

            // Render category circles
            if (grid) {
                grid.innerHTML = categories.map(c => `
                    <a href="products.html?category=${encodeURIComponent(c.name)}" class="cat-circle-card">
                        <div class="cat-img-wrapper">
                            <img src="${c.image || 'https://images.unsplash.com/photo-1542838132-92c53300491e?w=200'}" alt="${c.name}" loading="lazy">
                        </div>
                        <span class="cat-circle-name">${c.name}</span>
                    </a>
                `).join('');
            }

            // Render top ribbon items
            if (catNavList) {
                catNavList.innerHTML = `
                    <li class="cat-nav-item"><a href="products.html">⚡ All Deals</a></li>
                    ${categories.map(c => `
                        <li class="cat-nav-item"><a href="products.html?category=${encodeURIComponent(c.name)}">${c.name}</a></li>
                    `).join('')}
                `;
            }

            // Populate header search select
            if (catSelect) {
                catSelect.innerHTML = `
                    <option value="">All Categories</option>
                    ${categories.map(c => `<option value="${c.name}">${c.name}</option>`).join('')}
                `;
            }
        }
    } catch (e) {
        console.warn('Failed loading categories', e);
    }
}

async function loadFeaturedProducts() {
    const grid = document.getElementById('featured-grid');
    if (!grid) return;
    renderProductCards(grid, '/api/products/featured?limit=8');
}

async function loadTrendingProducts() {
    const grid = document.getElementById('trending-grid');
    if (!grid) return;
    renderProductCards(grid, '/api/products/trending?limit=8');
}

async function loadBestSellers() {
    const grid = document.getElementById('bestsellers-grid');
    if (!grid) return;
    renderProductCards(grid, '/api/products/bestsellers?limit=8');
}

async function renderProductCards(container, endpoint) {
    try {
        const res = await window.api.get(endpoint);
        if (res.success && res.data) {
            const products = res.data;
            container.innerHTML = products.map(p => createProductCardHtml(p)).join('');
        }
    } catch (e) {
        container.innerHTML = `<p style="grid-column: 1/-1; text-align: center; color: var(--text-muted); padding: 2rem;">Unable to load items at this moment.</p>`;
    }
}

function createProductCardHtml(p) {
    const discountBadge = p.discount && p.discount > 0 
        ? `<span class="discount-badge">${Math.round(p.discount)}% OFF</span>` 
        : '';

    const originalPriceHtml = p.discount && p.discount > 0 
        ? `<span class="original-price">${window.UI.formatCurrency(p.price)}</span>` 
        : '';

    const stockIndicator = p.stock > 5 
        ? `<span class="stock-indicator stock-in">● In Stock</span>` 
        : p.stock > 0 
            ? `<span class="stock-indicator stock-low">● Only ${p.stock} left!</span>` 
            : `<span class="stock-indicator stock-low">● Out of Stock</span>`;

    return `
        <div class="product-card">
            <div class="product-image-container">
                <a href="product-detail.html?id=${p.id}">
                    <img src="${p.imageUrl || 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500'}" alt="${p.name}" loading="lazy">
                </a>
                ${discountBadge}
                <button class="wishlist-heart-btn" title="Add to Wishlist" onclick="toggleWishlist(${p.id}, this)">
                    ♥
                </button>
            </div>
            <div class="product-info">
                <span class="product-brand">${p.brand || 'HariMart'}</span>
                <a href="product-detail.html?id=${p.id}" class="product-title" title="${p.name}">${p.name}</a>
                <div class="product-rating-bar">
                    <span class="rating-badge">★ ${p.rating ? p.rating.toFixed(1) : '4.5'}</span>
                    <span class="review-count">(${p.reviewCount || 12})</span>
                </div>
                <div class="product-price-wrap">
                    <span class="final-price">${window.UI.formatCurrency(p.finalPrice || p.price)}</span>
                    ${originalPriceHtml}
                </div>
                ${stockIndicator}
                <div class="card-actions">
                    <button class="btn-card-cart" onclick="window.Cart.addToCart(${p.id}, 1)">Add to Cart</button>
                    <button class="btn-card-buy" onclick="window.Cart.buyNow(${p.id}, 1)">Buy Now</button>
                </div>
            </div>
        </div>
    `;
}

async function toggleWishlist(productId, btn) {
    if (!window.Auth.isAuthenticated()) {
        window.UI.showToast('Please log in to save items to your wishlist', 'info');
        return;
    }
    try {
        await window.api.post(`/api/wishlist/${productId}`);
        btn.classList.toggle('active');
        window.UI.showToast('Added to Wishlist! ❤️', 'success');
    } catch (e) {
        window.UI.showToast(e.message || 'Error updating wishlist', 'error');
    }
}
