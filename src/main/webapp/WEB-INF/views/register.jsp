<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Account — HariMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/responsive.css">
</head>
<body style="background-color: var(--ivory-100); justify-content: center; align-items: center; min-height: 100vh; padding: 2.5rem 0;">

    <div style="width: 100%; max-width: 480px; margin: 0 auto; padding: 0 1rem;">
        <!-- Logo -->
        <div style="text-align: center; margin-bottom: 2rem;">
            <a href="${pageContext.request.contextPath}/" class="brand-logo" style="justify-content: center; display: inline-flex;">
                <div class="logo-symbol">H</div>
                <div class="brand-text">
                    <div class="brand-name" style="color: var(--navy-800);">Hari<span style="color: var(--amber-500);">Mart</span></div>
                    <svg class="brand-curve-arrow" viewBox="0 0 100 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M 5 5 Q 50 20 95 6" stroke="#f59e0b" stroke-width="3.5" stroke-linecap="round"/>
                        <path d="M 85 4 L 96 6 L 90 15" stroke="#f59e0b" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                </div>
            </a>
        </div>

        <div style="background-color: #ffffff; border-radius: var(--radius-xl); border: 1px solid var(--slate-200); padding: 2.25rem; box-shadow: var(--shadow-md);">
            <h2 style="font-size: 1.5rem; font-weight: 800; color: var(--navy-800); margin-bottom: 0.5rem;">Create Account</h2>
            <p style="color: var(--slate-500); font-size: 0.9rem; margin-bottom: 1.5rem;">Join HariMart to discover unmatched everyday deals</p>

            <c:if test="${not empty errorMessage}">
                <div style="background-color: var(--rose-100); color: var(--rose-600); padding: 0.75rem 1rem; border-radius: 8px; margin-bottom: 1.5rem; font-size: 0.88rem; font-weight: 600;">
                    ✕ ${errorMessage}
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/register" method="POST">
                <div class="form-group">
                    <label class="form-label">Your Name</label>
                    <input type="text" name="name" class="form-control" value="${name}" placeholder="First and last name" required autofocus>
                </div>

                <div class="form-group">
                    <label class="form-label">Mobile Number</label>
                    <input type="tel" name="phone" class="form-control" value="${phone}" placeholder="10-digit mobile number" required>
                </div>

                <div class="form-group">
                    <label class="form-label">Email Address</label>
                    <input type="email" name="email" class="form-control" value="${email}" placeholder="name@example.com" required>
                </div>

                <div class="form-group">
                    <label class="form-label">Password</label>
                    <input type="password" name="password" class="form-control" placeholder="At least 6 characters" minlength="6" required>
                </div>

                <div class="form-group">
                    <label class="form-label">I want to register as:</label>
                    <select name="role" class="form-control">
                        <option value="BUYER" ${role != 'SELLER' ? 'selected' : ''}>Shopper / Buyer</option>
                        <option value="SELLER" ${role == 'SELLER' ? 'selected' : ''}>Merchant / Seller (Sell products on HariMart)</option>
                    </select>
                </div>

                <button type="submit" class="btn-primary" style="width: 100%; justify-content: center; padding: 0.85rem; font-size: 1rem; margin-top: 0.5rem;">
                    Create Your HariMart Account
                </button>
            </form>

            <div style="text-align: center; margin-top: 1.5rem; font-size: 0.88rem; color: var(--slate-600);">
                Already have an account? 
                <a href="${pageContext.request.contextPath}/login" style="color: var(--navy-600); font-weight: 700;">Sign in</a>
            </div>
        </div>
    </div>

</body>
</html>
