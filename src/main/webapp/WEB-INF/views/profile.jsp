<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Profile — HariMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/responsive.css">
</head>
<body>

    <jsp:include page="includes/header.jsp"/>

    <main class="container" style="padding-top: 2.5rem; padding-bottom: 5rem; max-width: 800px;">

        <!-- Breadcrumbs -->
        <div style="font-size: 0.85rem; color: var(--slate-500); margin-bottom: 1.5rem;">
            <a href="${pageContext.request.contextPath}/">Home</a> &gt; 
            <span style="color: var(--slate-800); font-weight: 600;">My Account</span>
        </div>

        <h1 style="font-size: 1.85rem; font-weight: 800; color: var(--navy-800); margin-bottom: 1.5rem;">
            Account Settings & Profile
        </h1>

        <c:if test="${not empty successMessage}">
            <div style="background-color: var(--emerald-100); color: var(--emerald-600); padding: 0.85rem 1.25rem; border-radius: 8px; margin-bottom: 1.5rem; font-weight: 600;">
                ✓ ${successMessage}
            </div>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <div style="background-color: var(--rose-100); color: var(--rose-600); padding: 0.85rem 1.25rem; border-radius: 8px; margin-bottom: 1.5rem; font-weight: 600;">
                ✕ ${errorMessage}
            </div>
        </c:if>

        <div style="background-color: #ffffff; border-radius: var(--radius-xl); border: 1px solid var(--slate-200); padding: 2rem; box-shadow: var(--shadow-sm); margin-bottom: 2rem;">
            <div style="display: flex; align-items: center; gap: 1.25rem; margin-bottom: 2rem; padding-bottom: 1.5rem; border-bottom: 1px solid var(--slate-100);">
                <div style="width: 64px; height: 64px; background-color: var(--navy-800); color: #fff; font-size: 1.75rem; font-weight: 800; border-radius: var(--radius-full); display: flex; align-items: center; justify-content: center;">
                    ${user.name.substring(0, 1).toUpperCase()}
                </div>
                <div>
                    <h3 style="font-size: 1.25rem; font-weight: 800; color: var(--navy-800);">${user.name}</h3>
                    <div style="font-size: 0.85rem; color: var(--slate-500);">${user.email} • Role: <strong>${user.role}</strong></div>
                </div>
            </div>

            <form action="${pageContext.request.contextPath}/profile" method="POST">
                <div class="form-group">
                    <label class="form-label">Full Name</label>
                    <input type="text" name="name" class="form-control" value="${user.name}" required>
                </div>

                <div class="form-group">
                    <label class="form-label">Email Address (Immutable)</label>
                    <input type="email" class="form-control" value="${user.email}" disabled style="background-color: var(--slate-100);">
                </div>

                <div class="form-group">
                    <label class="form-label">Mobile Number</label>
                    <input type="tel" name="phone" class="form-control" value="${user.phone}" required>
                </div>

                <button type="submit" class="btn-primary" style="padding: 0.75rem 1.75rem; font-size: 0.95rem;">
                    Save Profile Changes
                </button>
            </form>
        </div>

        <!-- Quick Access Shortcuts -->
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 1.25rem;">
            <a href="${pageContext.request.contextPath}/orders" style="background-color: #fff; border: 1px solid var(--slate-200); padding: 1.25rem; border-radius: var(--radius-lg); display: block; text-decoration: none;">
                <div style="font-size: 1.5rem; margin-bottom: 0.5rem;">📦</div>
                <div style="font-weight: 700; color: var(--navy-800);">Your Orders</div>
                <div style="font-size: 0.85rem; color: var(--slate-500);">Track, return, or buy again</div>
            </a>
            <a href="${pageContext.request.contextPath}/addresses" style="background-color: #fff; border: 1px solid var(--slate-200); padding: 1.25rem; border-radius: var(--radius-lg); display: block; text-decoration: none;">
                <div style="font-size: 1.5rem; margin-bottom: 0.5rem;">📍</div>
                <div style="font-weight: 700; color: var(--navy-800);">Your Addresses</div>
                <div style="font-size: 0.85rem; color: var(--slate-500);">Edit delivery locations & default PIN</div>
            </a>
            <a href="${pageContext.request.contextPath}/wishlist" style="background-color: #fff; border: 1px solid var(--slate-200); padding: 1.25rem; border-radius: var(--radius-lg); display: block; text-decoration: none;">
                <div style="font-size: 1.5rem; margin-bottom: 0.5rem;">❤️</div>
                <div style="font-weight: 700; color: var(--navy-800);">Your Wishlist</div>
                <div style="font-size: 0.85rem; color: var(--slate-500);">View and manage saved products</div>
            </a>
        </div>

    </main>

    <jsp:include page="includes/footer.jsp"/>

</body>
</html>
