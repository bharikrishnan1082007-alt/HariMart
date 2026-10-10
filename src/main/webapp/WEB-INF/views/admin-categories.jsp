<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Categories — Admin Console — HariMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/responsive.css">
</head>
<body>

    <jsp:include page="includes/header.jsp"/>

    <div class="dashboard-container">
        <div class="dashboard-header">
            <div class="dashboard-title">
                <h2>Product Taxonomy & Category Management</h2>
                <p>Define marketplace department hierarchies and navigational taxonomy.</p>
            </div>
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn-action-sm" style="padding: 0.65rem 1.1rem;">
                ← Back to Admin Console
            </a>
        </div>

        <c:if test="${not empty param.msg}">
            <div style="background-color: var(--emerald-100); color: var(--emerald-600); padding: 0.85rem 1.25rem; border-radius: 8px; margin-bottom: 1.5rem; font-weight: 600;">
                ✓ ${param.msg}
            </div>
        </c:if>

        <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 2rem;">
            <!-- Category List -->
            <div class="data-card">
                <div class="data-card-header">
                    <h4>All Categories (${categories.size()})</h4>
                </div>
                <div class="data-table-wrapper">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Category</th>
                                <th>Description</th>
                                <th>Status</th>
                                <th style="text-align: right;">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="cat" items="${categories}">
                                <tr>
                                    <td>
                                        <div style="display: flex; align-items: center; gap: 0.75rem;">
                                            <c:if test="${not empty cat.imageUrl}">
                                                <img src="${cat.imageUrl}" alt="${cat.name}" style="width: 36px; height: 36px; object-fit: cover; border-radius: 6px;">
                                            </c:if>
                                            <div>
                                                <a href="${pageContext.request.contextPath}/category?id=${cat.id}" target="_blank" style="font-weight: 700; color: var(--navy-800); text-decoration: none;">
                                                    ${cat.name}
                                                </a>
                                                <div style="font-size: 0.78rem; color: var(--slate-400);">Slug: /category?id=${cat.id}</div>
                                            </div>
                                        </div>
                                    </td>
                                    <td style="color: var(--slate-600); font-size: 0.85rem; max-width: 250px;">
                                        ${cat.description}
                                    </td>
                                    <td>
                                        <span class="status-pill active">${cat.status}</span>
                                    </td>
                                    <td style="text-align: right;">
                                        <form action="${pageContext.request.contextPath}/admin/categories/delete" method="POST" style="display: inline;" onsubmit="return confirm('Delete this category?');">
                                            <input type="hidden" name="id" value="${cat.id}">
                                            <button type="submit" class="btn-action-sm" style="color: var(--rose-600);">
                                                Delete
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Add / Quick Category Form -->
            <div class="data-card">
                <div class="data-card-header">
                    <h4>Add Category</h4>
                </div>
                <div style="padding: 1.5rem;">
                    <form action="${pageContext.request.contextPath}/admin/categories/save" method="POST">
                        <div class="form-group">
                            <label class="form-label" for="name">Category Name *</label>
                            <input type="text" id="name" name="name" class="form-control" required placeholder="e.g. Smart Watches">
                        </div>
                        <div class="form-group">
                            <label class="form-label" for="imageUrl">Image URL</label>
                            <input type="url" id="imageUrl" name="imageUrl" class="form-control" placeholder="https://images.unsplash.com/photo-...">
                        </div>
                        <div class="form-group">
                            <label class="form-label" for="description">Short Description</label>
                            <textarea id="description" name="description" class="form-control" rows="3" placeholder="Category highlights..."></textarea>
                        </div>
                        <button type="submit" class="btn-primary" style="width: 100%; padding: 0.65rem;">
                            + Save Category
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="includes/footer.jsp"/>

</body>
</html>
