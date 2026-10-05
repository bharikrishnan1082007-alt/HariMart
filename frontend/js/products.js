/**
 * HariMart Product Catalog & Listing Controller
 */

let currentPage = 0;
const pageSize = 12;

document.addEventListener('DOMContentLoaded', async () => {
    parseUrlParams();
    await loadFilterCategories();
    await fetchProducts();
    setupEventListeners();
});

function parseUrlParams() {
    const urlParams = new URLSearchParams(window.location.search);
    const search = urlParams.get('search');
    const category = urlParams.get('category');
    const brand = urlParams.get('brand');

    if (search) {
        document.getElementById('catalog-search-input').value = search;
    }
    if (category) {
        document.getElementById('filter-category-select').value = category;
    }
    if (brand) {
        document.getElementById('filter-brand-input').value = brand;
    }
}

async function loadFilterCategories() {
    const select = document.getElementById('filter-category-select');
    if (!select) return;

    try {
        const res = await window.api.get('/api/categories');
        if (res.success && res.data) {
            const urlParams = new URLSearchParams(window.location.search);
            const currentCat = urlParams.get('category') || '';

            select.innerHTML = `<option value="">All Categories</option>` + 
                res.data.map(c => `<option value="${c.name}" ${c.name.toLowerCase() === currentCat.toLowerCase() ? 'selected' : ''}>${c.name}</option>`).join('');
        }
    } catch (e) {
        console.warn(e);
    }
}

async function fetchProducts(page = 0) {
    currentPage = page;
    const grid = document.getElementById('catalog-products-grid');
    const countEl = document.getElementById('catalog-result-count');
    const paginationContainer = document.getElementById('catalog-pagination');

    const search = document.getElementById('catalog-search-input').value.trim();
    const category = document.getElementById('filter-category-select').value;
    const brand = document.getElementById('filter-brand-input').value.trim();
    const minPrice = document.getElementById('filter-min-price').value;
    const maxPrice = document.getElementById('filter-max-price').value;
    const rating = document.getElementById('filter-rating-select').value;
    const inStock = document.getElementById('filter-instock-checkbox').checked;
    const sort = document.getElementById('catalog-sort-select').value;

    grid.innerHTML = `<div style="grid-column: 1/-1; text-align: center; padding: 4rem;"><span style="font-size: 2rem;">🔄</span><p style="color: var(--text-muted); margin-top: 0.5rem;">Loading HariMart Catalog...</p></div>`;

    const params = {
        page: currentPage,
        size: pageSize,
        sort: sort
    };
    if (search) params.search = search;
    if (category) params.category = category;
    if (brand) params.brand = brand;
    if (minPrice) params.minPrice = minPrice;
    if (maxPrice) params.maxPrice = maxPrice;
    if (rating) params.rating = rating;
    if (inStock) params.inStock = true;

    try {
        const res = await window.api.get('/api/products', params);
        if (res.success && res.data) {
            const pageData = res.data;
            const products = pageData.content || [];
            const totalElements = pageData.totalElements || products.length;
            const totalPages = pageData.totalPages || 1;

            if (countEl) {
                countEl.textContent = `Showing ${products.length} of ${totalElements} products`;
            }

            if (products.length === 0) {
                grid.innerHTML = `
                    <div style="grid-column: 1/-1; text-align: center; padding: 4rem 1rem; background: #FFFFFF; border-radius: var(--radius-md); border: 1px solid var(--border-color);">
                        <span style="font-size: 3rem;">🔍</span>
                        <h3 style="margin-top: 1rem; color: var(--secondary);">No products match your filters</h3>
                        <p style="color: var(--text-muted); margin-top: 0.5rem;">Try clearing some filters or searching with different keywords.</p>
                        <button onclick="resetFilters()" style="margin-top: 1.25rem; background: var(--primary); color:#FFF; border:none; padding: 0.6rem 1.5rem; border-radius: var(--radius-sm); font-weight:700; cursor:pointer;">Reset All Filters</button>
                    </div>
                `;
                if (paginationContainer) paginationContainer.innerHTML = '';
                return;
            }

            grid.innerHTML = products.map(p => createProductCardHtml(p)).join('');
            renderPagination(totalPages, currentPage);
        }
    } catch (e) {
        grid.innerHTML = `<p style="grid-column: 1/-1; text-align: center; color: var(--accent-crimson); padding: 3rem;">Error loading products: ${e.message}</p>`;
    }
}

function renderPagination(totalPages, activePage) {
    const container = document.getElementById('catalog-pagination');
    if (!container || totalPages <= 1) {
        if (container) container.innerHTML = '';
        return;
    }

    let html = '';
    html += `<button class="page-btn ${activePage === 0 ? 'disabled' : ''}" onclick="fetchProducts(${activePage - 1})" ${activePage === 0 ? 'disabled' : ''}>« Prev</button>`;

    for (let i = 0; i < totalPages; i++) {
        if (i === 0 || i === totalPages - 1 || (i >= activePage - 2 && i <= activePage + 2)) {
            html += `<button class="page-btn ${i === activePage ? 'active' : ''}" onclick="fetchProducts(${i})">${i + 1}</button>`;
        } else if (i === activePage - 3 || i === activePage + 3) {
            html += `<span style="padding: 0 4px; color: var(--text-muted);">...</span>`;
        }
    }

    html += `<button class="page-btn ${activePage === totalPages - 1 ? 'disabled' : ''}" onclick="fetchProducts(${activePage + 1})" ${activePage === totalPages - 1 ? 'disabled' : ''}>Next »</button>`;
    container.innerHTML = html;
}

function resetFilters() {
    document.getElementById('catalog-search-input').value = '';
    document.getElementById('filter-category-select').value = '';
    document.getElementById('filter-brand-input').value = '';
    document.getElementById('filter-min-price').value = '';
    document.getElementById('filter-max-price').value = '';
    document.getElementById('filter-rating-select').value = '';
    document.getElementById('filter-instock-checkbox').checked = false;
    document.getElementById('catalog-sort-select').value = 'newest';
    fetchProducts(0);
}

function setupEventListeners() {
    const filterForm = document.getElementById('catalog-filters-form');
    if (filterForm) {
        filterForm.addEventListener('submit', (e) => {
            e.preventDefault();
            fetchProducts(0);
        });
    }

    const sortSelect = document.getElementById('catalog-sort-select');
    if (sortSelect) {
        sortSelect.addEventListener('change', () => fetchProducts(0));
    }

    const resetBtn = document.getElementById('reset-filters-btn');
    if (resetBtn) {
        resetBtn.addEventListener('click', resetFilters);
    }
}
