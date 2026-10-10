<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${product.id != null ? 'Edit Product' : 'Add New Product'} — Seller Central — HariMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/responsive.css">
</head>
<body>

    <jsp:include page="includes/header.jsp"/>

    <div class="dashboard-container" style="max-width: 900px;">
        <div class="dashboard-header">
            <div class="dashboard-title">
                <h2>${product.id != null ? 'Edit Product Listing' : 'Create New Product Listing'}</h2>
                <p>Provide detailed specifications and pricing to maximize buyer engagement.</p>
            </div>
            <a href="${pageContext.request.contextPath}/seller/products" class="btn-action-sm" style="padding: 0.65rem 1.1rem;">
                ← Cancel & Return
            </a>
        </div>

        <div class="data-card" style="padding: 2.25rem;">
            <form action="${pageContext.request.contextPath}/seller/products/save" method="POST">
                <c:if test="${product.id != null}">
                    <input type="hidden" name="id" value="${product.id}">
                </c:if>

                <div class="form-group">
                    <label class="form-label" for="name">Product Title / Name *</label>
                    <input type="text" id="name" name="name" class="form-control" value="${product.name}" required placeholder="e.g. BoAt Rockerz 450 Bluetooth On Ear Headphones with Mic">
                </div>

                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem;">
                    <div class="form-group">
                        <label class="form-label" for="categoryId">Category *</label>
                        <select id="categoryId" name="categoryId" class="form-control" required>
                            <option value="">-- Select Category --</option>
                            <c:forEach var="cat" items="${categories}">
                                <option value="${cat.id}" ${product.categoryId == cat.id ? 'selected' : ''}>${cat.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="brand">Brand *</label>
                        <input type="text" id="brand" name="brand" class="form-control" value="${product.brand}" required placeholder="e.g. boAt, Samsung, Nike">
                    </div>
                </div>

                <div style="display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 1.5rem;">
                    <div class="form-group">
                        <label class="form-label" for="price">Regular MRP Price (₹) *</label>
                        <input type="number" step="0.01" id="price" name="price" class="form-control" value="${product.price}" required placeholder="1999.00">
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="discountPrice">Deal / Sale Price (₹)</label>
                        <input type="number" step="0.01" id="discountPrice" name="discountPrice" class="form-control" value="${product.discountPrice}" placeholder="1499.00 (optional)">
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="stockQuantity">Stock Quantity *</label>
                        <input type="number" min="0" id="stockQuantity" name="stockQuantity" class="form-control" value="${product.stockQuantity != null ? product.stockQuantity : 10}" required placeholder="50">
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label" for="imageUrl">Main Image URL *</label>
                    <input type="url" id="imageUrl" name="imageUrl" class="form-control" value="${product.imageUrl}" required placeholder="https://images.unsplash.com/photo-...">
                    <small style="color: var(--slate-400); font-size: 0.8rem; margin-top: 0.35rem; display: block;">Provide an HTTPS image link (Unsplash or CDN recommended).</small>
                </div>

                <div class="form-group">
                    <label class="form-label" for="description">Detailed Description</label>
                    <textarea id="description" name="description" class="form-control" rows="6" placeholder="Describe the product key features, specifications, box contents, and warranties...">${product.description}</textarea>
                </div>

                <div style="display: flex; gap: 2rem; margin-bottom: 2rem; padding: 1rem; background-color: var(--ivory-50); border-radius: 8px; border: 1px solid var(--slate-200);">
                    <label style="display: flex; align-items: center; gap: 0.6rem; cursor: pointer; font-weight: 600; color: var(--navy-800);">
                        <input type="checkbox" name="isActive" value="true" ${product.id == null || product.isActive ? 'checked' : ''} style="width: 18px; height: 18px;">
                        Active & Available for Sale
                    </label>
                    <label style="display: flex; align-items: center; gap: 0.6rem; cursor: pointer; font-weight: 600; color: var(--navy-800);">
                        <input type="checkbox" name="isFeatured" value="true" ${product.isFeatured ? 'checked' : ''} style="width: 18px; height: 18px;">
                        Promote as Featured Item
                    </label>
                </div>

                <div style="display: flex; justify-content: flex-end; gap: 1rem;">
                    <a href="${pageContext.request.contextPath}/seller/products" class="btn-action-sm" style="padding: 0.75rem 1.5rem;">
                        Cancel
                    </a>
                    <button type="submit" class="btn-primary" style="padding: 0.75rem 2rem; font-size: 0.95rem;">
                        ${product.id != null ? 'Update Listing' : 'Publish Product'}
                    </button>
                </div>
            </form>
        </div>
    </div>

    <jsp:include page="includes/footer.jsp"/>

</body>
</html>
