<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Become a Seller — HariMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/responsive.css">
</head>
<body>

    <jsp:include page="includes/header.jsp"/>

    <main class="container" style="padding-top: 3rem; padding-bottom: 5rem; max-width: 800px;">

        <div style="text-align: center; margin-bottom: 2.5rem;">
            <div style="font-size: 0.85rem; font-weight: 700; color: var(--amber-500); text-transform: uppercase; letter-spacing: 0.05em; margin-bottom: 0.5rem;">
                HariMart Merchant Onboarding
            </div>
            <h1 style="font-size: 2.25rem; font-weight: 900; color: var(--navy-800);">
                Grow Your Business on HariMart
            </h1>
            <p style="color: var(--slate-500); font-size: 1.05rem; max-width: 600px; margin: 0.5rem auto 0;">
                Reach millions of online shoppers across India with transparent 10% platform commission and fast weekly payouts.
            </p>
        </div>

        <c:if test="${not empty param.err}">
            <div style="background-color: var(--rose-100); color: var(--rose-600); padding: 0.85rem 1.25rem; border-radius: 8px; margin-bottom: 1.5rem; font-weight: 600;">
                ✕ ${param.err}
            </div>
        </c:if>

        <div style="background-color: #ffffff; border-radius: var(--radius-xl); border: 1px solid var(--slate-200); padding: 2.5rem; box-shadow: var(--shadow-sm);">
            <h3 style="font-size: 1.25rem; font-weight: 800; color: var(--navy-800); margin-bottom: 1.5rem; border-bottom: 1px solid var(--slate-100); padding-bottom: 0.75rem;">
                Merchant Registration Details
            </h3>

            <form action="${pageContext.request.contextPath}/seller/register" method="POST">
                <div class="form-group">
                    <label class="form-label">Store / Company Name</label>
                    <input type="text" name="storeName" class="form-control" placeholder="e.g. Bharat Artisans Hub" required autofocus>
                </div>

                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                    <div class="form-group">
                        <label class="form-label">Business Email</label>
                        <input type="email" name="businessEmail" class="form-control" placeholder="sales@yourstore.in" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Business Phone Number</label>
                        <input type="tel" name="phone" class="form-control" placeholder="10-digit mobile number" required>
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label">GSTIN / Tax ID Number</label>
                    <input type="text" name="taxId" class="form-control" placeholder="e.g. GSTIN29AAAAA0000A1Z5" required>
                </div>

                <div class="form-group">
                    <label class="form-label">Warehouse / Pickup Address</label>
                    <textarea name="address" class="form-control" rows="3" placeholder="Full pickup address for logistics partner..." required></textarea>
                </div>

                <div style="background-color: var(--ivory-50); border: 1px solid var(--slate-200); padding: 1rem; border-radius: var(--radius-md); font-size: 0.85rem; color: var(--slate-600); margin-bottom: 1.5rem;">
                    🛡️ By registering, you agree to comply with HariMart's Merchant Service Agreement, 10% platform commission schedule, and 7-day customer return policy.
                </div>

                <button type="submit" class="btn-primary" style="width: 100%; justify-content: center; padding: 0.95rem; font-size: 1.05rem;">
                    🚀 Register & Open Merchant Store
                </button>
            </form>
        </div>

    </main>

    <jsp:include page="includes/footer.jsp"/>

</body>
</html>
