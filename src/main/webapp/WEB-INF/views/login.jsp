<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign In — HariMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/responsive.css">
</head>
<body style="background-color: var(--ivory-100); justify-content: center; align-items: center; min-height: 100vh; padding: 2rem 0;">

    <div style="width: 100%; max-width: 440px; margin: 0 auto; padding: 0 1rem;">
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
            <h2 style="font-size: 1.5rem; font-weight: 800; color: var(--navy-800); margin-bottom: 0.5rem;">Sign In</h2>
            <p style="color: var(--slate-500); font-size: 0.9rem; margin-bottom: 1.5rem;">Access your HariMart account, orders & wishlist</p>

            <c:if test="${not empty errorMessage}">
                <div style="background-color: var(--rose-100); color: var(--rose-600); padding: 0.75rem 1rem; border-radius: 8px; margin-bottom: 1.5rem; font-size: 0.88rem; font-weight: 600;">
                    ✕ ${errorMessage}
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/login" method="POST" id="loginForm">
                <input type="hidden" name="returnUrl" value="${returnUrl}">

                <div class="form-group">
                    <label class="form-label">Email Address</label>
                    <input type="email" name="email" id="loginEmail" class="form-control" value="${email}" placeholder="name@example.com" required autofocus>
                </div>

                <div class="form-group">
                    <div style="display: flex; justify-content: space-between; align-items: baseline;">
                        <label class="form-label">Password</label>
                        <a href="javascript:void(0)" onclick="alert('For demo accounts, use password: buyer123, seller123, or admin123');" style="font-size: 0.8rem; color: var(--navy-600);">Forgot password?</a>
                    </div>
                    <input type="password" name="password" id="loginPassword" class="form-control" placeholder="••••••••" required>
                </div>

                <button type="submit" class="btn-primary" style="width: 100%; justify-content: center; padding: 0.85rem; font-size: 1rem; margin-top: 0.5rem;">
                    Sign In
                </button>
            </form>

            <!-- 1-Click Demo Logins -->
            <div style="margin-top: 1.75rem; border-top: 1px dashed var(--slate-200); padding-top: 1.25rem;">
                <div style="font-size: 0.78rem; text-transform: uppercase; font-weight: 700; color: var(--slate-400); text-align: center; margin-bottom: 0.75rem; letter-spacing: 0.05em;">
                    ⚡ 1-Click Quick Demo Sign In
                </div>
                <div style="display: flex; gap: 0.5rem; justify-content: center;">
                    <button type="button" class="btn-action-sm" onclick="setDemo('buyer@harimart.com', 'buyer123')" style="flex: 1; justify-content: center;">
                        👤 Buyer
                    </button>
                    <button type="button" class="btn-action-sm" onclick="setDemo('seller1@harimart.com', 'seller123')" style="flex: 1; justify-content: center;">
                        🏪 Seller
                    </button>
                    <button type="button" class="btn-action-sm" onclick="setDemo('admin@harimart.com', 'admin123')" style="flex: 1; justify-content: center;">
                        🛡️ Admin
                    </button>
                </div>
            </div>

            <div style="text-align: center; margin-top: 1.5rem; font-size: 0.88rem; color: var(--slate-600);">
                New to HariMart? 
                <a href="${pageContext.request.contextPath}/register" style="color: var(--navy-600); font-weight: 700;">Create an account</a>
            </div>
        </div>
    </div>

    <script>
        function setDemo(email, pass) {
            document.getElementById('loginEmail').value = email;
            document.getElementById('loginPassword').value = pass;
            document.getElementById('loginForm').submit();
        }
    </script>

</body>
</html>
