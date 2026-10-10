<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Financial & Sales Analytics — Seller Central — HariMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/responsive.css">
</head>
<body>

    <jsp:include page="includes/header.jsp"/>

    <div class="dashboard-container">
        <div class="dashboard-header">
            <div class="dashboard-title">
                <h2>Merchant Performance & Financial Analytics</h2>
                <p>Detailed breakdown of your gross sales, marketplace commission deductions, and net payouts.</p>
            </div>
            <a href="${pageContext.request.contextPath}/seller/dashboard" class="btn-action-sm" style="padding: 0.65rem 1.1rem;">
                ← Back to Dashboard
            </a>
        </div>

        <!-- KPI Grid -->
        <div class="metrics-grid">
            <div class="metric-card">
                <div class="metric-icon-box amber">💰</div>
                <div class="metric-info">
                    <h6>Gross Sales</h6>
                    <h3>${analytics.formattedRevenue}</h3>
                </div>
            </div>
            <div class="metric-card">
                <div class="metric-icon-box rose">🏷</div>
                <div class="metric-info">
                    <h6>Platform Fee (${seller.commissionRate}%)</h6>
                    <h3>${analytics.formattedCommission}</h3>
                </div>
            </div>
            <div class="metric-card">
                <div class="metric-icon-box emerald">💵</div>
                <div class="metric-info">
                    <h6>Net Payout Earnings</h6>
                    <h3>${analytics.formattedSellerEarnings}</h3>
                </div>
            </div>
            <div class="metric-card">
                <div class="metric-icon-box blue">📦</div>
                <div class="metric-info">
                    <h6>Total Ordered Items</h6>
                    <h3>${analytics.totalOrders}</h3>
                </div>
            </div>
        </div>

        <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 2rem;">
            <!-- Financial Breakdown Table -->
            <div class="data-card">
                <div class="data-card-header">
                    <h4>Payout Accounting Statement</h4>
                    <span class="status-pill active">Automatic Bi-Weekly Settlement</span>
                </div>
                <div style="padding: 1.75rem;">
                    <div style="display: flex; justify-content: space-between; padding: 0.85rem 0; border-bottom: 1px solid var(--slate-100); font-size: 0.95rem;">
                        <span style="color: var(--slate-600);">Gross GMV (Delivered & In-Transit Orders)</span>
                        <strong style="color: var(--navy-900);">${analytics.formattedRevenue}</strong>
                    </div>
                    <div style="display: flex; justify-content: space-between; padding: 0.85rem 0; border-bottom: 1px solid var(--slate-100); font-size: 0.95rem;">
                        <span style="color: var(--slate-600);">Marketplace Facilitation Fee (${seller.commissionRate}%)</span>
                        <strong style="color: var(--rose-600);">- ${analytics.formattedCommission}</strong>
                    </div>
                    <div style="display: flex; justify-content: space-between; padding: 0.85rem 0; border-bottom: 1px solid var(--slate-100); font-size: 0.95rem;">
                        <span style="color: var(--slate-600);">Payment Gateway Processing (Included)</span>
                        <strong style="color: var(--emerald-600);">₹0.00</strong>
                    </div>
                    <div style="display: flex; justify-content: space-between; padding: 1.25rem 0; margin-top: 0.5rem; font-size: 1.15rem; border-top: 2px dashed var(--slate-200);">
                        <strong style="color: var(--navy-900);">Net Withdrawable Payout</strong>
                        <strong style="color: var(--emerald-600); font-size: 1.35rem;">${analytics.formattedSellerEarnings}</strong>
                    </div>

                    <div style="background-color: var(--ivory-100); padding: 1rem 1.25rem; border-radius: 8px; margin-top: 1.25rem; font-size: 0.88rem; color: var(--slate-600);">
                        💡 HariMart automatically initiates bank transfers every alternate Wednesday for all verified delivered items.
                    </div>
                </div>
            </div>

            <!-- Merchant Health Card -->
            <div class="data-card">
                <div class="data-card-header">
                    <h4>Store Health & Status</h4>
                </div>
                <div style="padding: 1.75rem;">
                    <div style="margin-bottom: 1.25rem;">
                        <label style="font-size: 0.85rem; color: var(--slate-500); font-weight: 600;">KYC & Account Status</label>
                        <div style="margin-top: 0.25rem;">
                            <span class="status-pill approved" style="font-size: 0.85rem;">Verified Merchant</span>
                        </div>
                    </div>
                    <div style="margin-bottom: 1.25rem;">
                        <label style="font-size: 0.85rem; color: var(--slate-500); font-weight: 600;">Active Listings</label>
                        <div style="font-size: 1.2rem; font-weight: 700; color: var(--navy-800); margin-top: 0.2rem;">
                            ${analytics.totalProducts} Items
                        </div>
                    </div>
                    <div style="margin-bottom: 1.25rem;">
                        <label style="font-size: 0.85rem; color: var(--slate-500); font-weight: 600;">Fulfillment SLA</label>
                        <div style="font-size: 1.1rem; font-weight: 700; color: var(--emerald-600); margin-top: 0.2rem;">
                            99.4% On-Time Dispatch
                        </div>
                    </div>
                    <div style="padding-top: 1rem; border-top: 1px solid var(--slate-200);">
                        <a href="${pageContext.request.contextPath}/seller/products" class="btn-primary" style="display: block; text-align: center; padding: 0.65rem 1rem; font-size: 0.88rem;">
                            Manage Inventory
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="includes/footer.jsp"/>

</body>
</html>
