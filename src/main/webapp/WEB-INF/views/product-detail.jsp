<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${product.name} — HariMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/responsive.css">
    <style>
        .product-detail-layout {
            display: grid;
            grid-template-columns: 1fr 1.15fr;
            gap: 3.5rem;
            margin-top: 2rem;
            margin-bottom: 4rem;
            background: #ffffff;
            padding: 2.5rem;
            border-radius: var(--radius-xl);
            border: 1px solid var(--slate-200);
            box-shadow: var(--shadow-sm);
        }
        .main-gallery-img {
            width: 100%;
            max-height: 500px;
            object-fit: cover;
            border-radius: var(--radius-lg);
            border: 1px solid var(--slate-200);
        }
        .stock-pill {
            display: inline-flex;
            padding: 0.3rem 0.75rem;
            border-radius: var(--radius-full);
            font-size: 0.82rem;
            font-weight: 700;
        }
        .stock-in { background-color: var(--emerald-100); color: var(--emerald-600); }
        .stock-low { background-color: var(--amber-100); color: var(--amber-500); }
        .stock-out { background-color: var(--rose-100); color: var(--rose-600); }
        .reviews-section {
            background-color: #ffffff;
            border-radius: var(--radius-xl);
            padding: 2.5rem;
            border: 1px solid var(--slate-200);
            margin-bottom: 4rem;
        }
        @media(max-width: 900px) {
            .product-detail-layout { grid-template-columns: 1fr; gap: 2rem; padding: 1.5rem; }
        }
    </style>
</head>
<body>

    <jsp:include page="includes/header.jsp"/>

    <main class="container" style="padding-top: 1.5rem;">

        <!-- Breadcrumbs -->
        <div style="font-size: 0.85rem; color: var(--slate-500); margin-bottom: 1rem;">
            <a href="${pageContext.request.contextPath}/">Home</a> &gt; 
            <a href="${pageContext.request.contextPath}/category?slug=${product.categoryName}">${product.categoryName}</a> &gt; 
            <span style="color: var(--slate-800); font-weight: 600;">${product.name}</span>
        </div>

        <c:if test="${not empty param.msg}">
            <div style="background-color: var(--emerald-100); color: var(--emerald-600); padding: 0.85rem 1.25rem; border-radius: 8px; margin-bottom: 1.5rem; font-weight: 600;">
                ✓ ${param.msg}
            </div>
        </c:if>
        <c:if test="${not empty param.err}">
            <div style="background-color: var(--rose-100); color: var(--rose-600); padding: 0.85rem 1.25rem; border-radius: 8px; margin-bottom: 1.5rem; font-weight: 600;">
                ✕ ${param.err}
            </div>
        </c:if>

        <!-- Product Presentation Grid -->
        <div class="product-detail-layout">
            <!-- Left: Gallery -->
            <div>
                <img src="${product.imageUrl}" alt="${product.name}" class="main-gallery-img" id="mainProdImg">
                <c:if test="${not empty product.additionalImages}">
                    <div style="display: flex; gap: 0.75rem; margin-top: 1rem; overflow-x: auto;">
                        <img src="${product.imageUrl}" style="width: 70px; height: 70px; object-fit: cover; border-radius: 8px; cursor: pointer; border: 2px solid var(--amber-400);" 
                             onclick="document.getElementById('mainProdImg').src = this.src">
                        <c:forEach var="extraImg" items="${product.additionalImages.split(',')}">
                            <c:if test="${not empty extraImg.trim()}">
                                <img src="${extraImg.trim()}" style="width: 70px; height: 70px; object-fit: cover; border-radius: 8px; cursor: pointer; border: 1px solid var(--slate-200);"
                                     onclick="document.getElementById('mainProdImg').src = this.src">
                            </c:if>
                        </c:forEach>
                    </div>
                </c:if>
            </div>

            <!-- Right: Details, Pricing, Actions -->
            <div>
                <div style="font-size: 0.85rem; font-weight: 700; color: var(--slate-400); text-transform: uppercase; letter-spacing: 0.05em; margin-bottom: 0.35rem;">
                    Brand: <strong style="color: var(--navy-800);">${product.brand}</strong> | Category: ${product.categoryName}
                </div>
                <h1 style="font-size: 1.85rem; font-weight: 800; color: var(--navy-800); line-height: 1.25; margin-bottom: 0.75rem;">
                    ${product.name}
                </h1>

                <!-- Rating & Seller -->
                <div style="display: flex; align-items: center; gap: 1rem; margin-bottom: 1.25rem; font-size: 0.9rem;">
                    <div style="color: var(--amber-500); font-weight: 700;">
                        ★ ${product.rating} <span style="color: var(--slate-500); font-weight: normal;">(${product.reviewCount} customer reviews)</span>
                    </div>
                    <div style="color: var(--slate-400);">•</div>
                    <div>Sold by: <strong style="color: var(--navy-600);">${product.sellerStoreName}</strong> (Verified Merchant)</div>
                </div>

                <!-- Pricing Box -->
                <div style="background-color: var(--ivory-50); border: 1px solid var(--slate-200); padding: 1.25rem; border-radius: var(--radius-md); margin-bottom: 1.5rem;">
                    <div style="display: flex; align-items: baseline; gap: 0.75rem;">
                        <span style="font-size: 2.25rem; font-weight: 900; color: var(--navy-800);">${product.formattedPrice}</span>
                        <c:if test="${product.hasDiscount()}">
                            <span style="font-size: 1.25rem; color: var(--slate-400); text-decoration: line-through;">${product.formattedOriginalPrice}</span>
                            <span style="background-color: var(--rose-600); color: #fff; padding: 0.2rem 0.5rem; border-radius: 6px; font-size: 0.85rem; font-weight: 700;">
                                ${product.discountPercentage}% OFF
                            </span>
                        </c:if>
                    </div>
                    <div style="font-size: 0.85rem; color: var(--emerald-600); font-weight: 600; margin-top: 0.35rem;">
                        Inclusive of all taxes. Free shipping on orders above ₹499.
                    </div>
                </div>

                <!-- Stock Status -->
                <div style="margin-bottom: 1.5rem;">
                    <c:choose>
                        <c:when test="${product.stockQuantity > 5}">
                            <span class="stock-pill stock-in">✓ In Stock (${product.stockQuantity} units available)</span>
                        </c:when>
                        <c:when test="${product.stockQuantity > 0}">
                            <span class="stock-pill stock-low">⚡ Only ${product.stockQuantity} left in stock - order soon!</span>
                        </c:when>
                        <c:otherwise>
                            <span class="stock-pill stock-out">✕ Currently Out of Stock</span>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Description -->
                <div style="margin-bottom: 2rem;">
                    <h4 style="font-size: 1.05rem; font-weight: 700; margin-bottom: 0.5rem; color: var(--navy-800);">About this item</h4>
                    <p style="color: var(--slate-700); font-size: 0.95rem; line-height: 1.6;">
                        ${product.description}
                    </p>
                </div>

                <!-- Buying Actions Form -->
                <c:if test="${product.isInStock()}">
                    <form action="${pageContext.request.contextPath}/checkout" method="GET" style="margin-bottom: 1.5rem;">
                        <input type="hidden" name="buyNowProductId" value="${product.id}">
                        <div style="display: flex; align-items: center; gap: 1rem; margin-bottom: 1.25rem;">
                            <label style="font-weight: 600; font-size: 0.9rem;">Quantity:</label>
                            <select name="buyNowQuantity" id="prodDetailQuantity" style="padding: 0.45rem 1rem; border-radius: 8px; border: 1px solid var(--slate-300); font-weight: 600;">
                                <c:forEach var="q" begin="1" end="${product.stockQuantity > 10 ? 10 : product.stockQuantity}">
                                    <option value="${q}">${q}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div style="display: flex; gap: 1rem; flex-wrap: wrap;">
                            <button type="button" class="btn-primary" style="flex: 1; justify-content: center;"
                                    onclick="addToCartAjax(${product.id}, document.getElementById('prodDetailQuantity').value, '${pageContext.request.contextPath}')">
                                🛒 Add to Cart
                            </button>
                            <button type="submit" class="btn-secondary" style="flex: 1; justify-content: center; background-color: var(--navy-800); color: #fff; border: none;">
                                ⚡ Buy Now
                            </button>
                        </div>
                    </form>
                </c:if>

                <!-- Wishlist Toggle -->
                <button type="button" class="wishlist-toggle-btn btn-action-sm" data-product-id="${product.id}" data-context-path="${pageContext.request.contextPath}" style="width: 100%; justify-content: center; padding: 0.65rem;">
                    ❤️ Add to Wishlist
                </button>
            </div>
        </div>

        <!-- Customer Reviews Section -->
        <div class="reviews-section">
            <div class="section-header">
                <div>
                    <h3 class="section-title">Verified Customer Reviews</h3>
                    <p class="section-subtitle">Real feedback from verified purchasers across India</p>
                </div>
            </div>

            <!-- Review Submission Form -->
            <c:choose>
                <c:when test="${not empty sessionScope.currentUser}">
                    <form action="${pageContext.request.contextPath}/review/add" method="POST" style="background-color: var(--ivory-50); border: 1px solid var(--slate-200); padding: 1.5rem; border-radius: var(--radius-md); margin-bottom: 2.5rem;">
                        <input type="hidden" name="productId" value="${product.id}">
                        <h4 style="font-size: 1.1rem; font-weight: 700; margin-bottom: 1rem;">Write a Customer Review</h4>
                        <div style="display: grid; grid-template-columns: 180px 1fr; gap: 1rem; margin-bottom: 1rem;">
                            <div>
                                <label class="form-label">Rating</label>
                                <select name="rating" class="form-control" required>
                                    <option value="5">★★★★★ (5 Stars)</option>
                                    <option value="4">★★★★☆ (4 Stars)</option>
                                    <option value="3">★★★☆☆ (3 Stars)</option>
                                    <option value="2">★★☆☆☆ (2 Stars)</option>
                                    <option value="1">★☆☆☆☆ (1 Star)</option>
                                </select>
                            </div>
                            <div>
                                <label class="form-label">Review Title</label>
                                <input type="text" name="title" class="form-control" placeholder="Headline for your review" required>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Your Experience</label>
                            <textarea name="comment" class="form-control" rows="3" placeholder="What did you like or dislike about this product?" required></textarea>
                        </div>
                        <button type="submit" class="btn-primary" style="padding: 0.65rem 1.5rem; font-size: 0.9rem;">Submit Review</button>
                    </form>
                </c:when>
                <c:otherwise>
                    <div style="background-color: var(--ivory-100); padding: 1.25rem; border-radius: var(--radius-md); text-align: center; margin-bottom: 2rem;">
                        Please <a href="${pageContext.request.contextPath}/login" style="color: var(--navy-600); font-weight: 700;">sign in</a> to submit a verified rating and review.
                    </div>
                </c:otherwise>
            </c:choose>

            <!-- Reviews Listing -->
            <c:choose>
                <c:when test="${not empty reviews}">
                    <div style="display: flex; flex-direction: column; gap: 1.5rem;">
                        <c:forEach var="rev" items="${reviews}">
                            <div style="border-bottom: 1px solid var(--slate-200); padding-bottom: 1.25rem;">
                                <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 0.35rem;">
                                    <div style="font-weight: 700; font-size: 0.95rem; color: var(--navy-800);">${rev.userName}</div>
                                    <div style="color: var(--amber-500); font-weight: 700; font-size: 0.9rem;">
                                        <c:forEach begin="1" end="${rev.rating}">★</c:forEach>
                                    </div>
                                </div>
                                <div style="font-weight: 700; font-size: 0.95rem; margin-bottom: 0.25rem;">${rev.title}</div>
                                <p style="color: var(--slate-700); font-size: 0.9rem; line-height: 1.5;">${rev.comment}</p>
                                <c:if test="${rev.isVerifiedPurchase}">
                                    <span style="font-size: 0.75rem; color: var(--emerald-600); font-weight: 700;">✓ Verified Purchase</span>
                                </c:if>
                            </div>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <p style="color: var(--slate-500); font-style: italic;">No reviews submitted yet. Be the first to share your thoughts!</p>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- Related Products Carousel/Grid -->
        <c:if test="${not empty relatedProducts}">
            <div class="section-header">
                <div>
                    <h3 class="section-title">Similar Items You Might Like</h3>
                    <p class="section-subtitle">Based on this product's category</p>
                </div>
            </div>
            <div class="products-grid">
                <c:forEach var="p" items="${relatedProducts}">
                    <div class="product-card">
                        <div class="product-thumb-container">
                            <a href="${pageContext.request.contextPath}/product?id=${p.id}" style="width: 100%; height: 100%;">
                                <img src="${p.imageUrl}" alt="${p.name}" class="product-thumb">
                            </a>
                        </div>
                        <div class="product-content">
                            <a href="${pageContext.request.contextPath}/product?id=${p.id}" class="product-title">${p.name}</a>
                            <div class="product-price-row">
                                <span class="price-current">${p.formattedPrice}</span>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:if>

    </main>

    <jsp:include page="includes/footer.jsp"/>

</body>
</html>
