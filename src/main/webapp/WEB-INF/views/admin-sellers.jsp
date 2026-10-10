<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Seller Management — Admin Console — HariMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/responsive.css">
</head>
<body>

    <jsp:include page="includes/header.jsp"/>

    <div class="dashboard-container">
        <div class="dashboard-header">
            <div class="dashboard-title">
                <h2>Merchant Partners & Onboarding</h2>
                <p>Approve merchant applications, monitor compliance, and set commission policies.</p>
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

        <div class="data-card">
            <div class="data-card-header">
                <h4>Registered Sellers (${sellers.size()})</h4>
            </div>
            <div class="data-table-wrapper">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Store Details</th>
                            <th>Owner</th>
                            <th>GSTIN / Tax ID</th>
                            <th>Contact</th>
                            <th>Fee</th>
                            <th>Status</th>
                            <th style="text-align: right;">Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty sellers}">
                                <tr>
                                    <td colspan="7" style="text-align: center; padding: 3rem; color: var(--slate-400);">
                                        No registered sellers found.
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="s" items="${sellers}">
                                    <tr>
                                        <td>
                                            <div style="font-weight: 700; color: var(--navy-900); font-size: 1rem;">${s.storeName}</div>
                                            <div style="font-size: 0.82rem; color: var(--slate-500);">${s.businessEmail}</div>
                                        </td>
                                        <td>
                                            <div style="font-weight: 600; color: var(--navy-800);">${s.ownerName != null ? s.ownerName : ('User #'.concat(s.userId))}</div>
                                        </td>
                                        <td>
                                            <span style="font-family: monospace; font-size: 0.85rem; background-color: var(--slate-100); padding: 0.2rem 0.5rem; border-radius: 4px;">
                                                ${s.taxId != null ? s.taxId : 'PENDING-KYC'}
                                            </span>
                                        </td>
                                        <td>
                                            <div style="font-size: 0.85rem;">${s.phone != null ? s.phone : '—'}</div>
                                            <div style="font-size: 0.78rem; color: var(--slate-400); max-width: 150px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">${s.address}</div>
                                        </td>
                                        <td>
                                            <strong>${s.commissionRate}%</strong>
                                        </td>
                                        <td>
                                            <span class="status-pill ${s.status.toLowerCase()}">
                                                ${s.status}
                                            </span>
                                        </td>
                                        <td style="text-align: right;">
                                            <form action="${pageContext.request.contextPath}/admin/sellers/status" method="POST" style="display: inline-flex; gap: 0.4rem;">
                                                <input type="hidden" name="sellerId" value="${s.id}">
                                                <c:choose>
                                                    <c:when test="${s.status == 'APPROVED'}">
                                                        <input type="hidden" name="status" value="SUSPENDED">
                                                        <button type="submit" class="btn-action-sm" style="color: var(--rose-600); border-color: var(--rose-200);">
                                                            Suspend
                                                        </button>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <input type="hidden" name="status" value="APPROVED">
                                                        <button type="submit" class="btn-action-sm" style="color: var(--emerald-600); border-color: var(--emerald-200);">
                                                            Approve
                                                        </button>
                                                    </c:otherwise>
                                                </c:choose>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <jsp:include page="includes/footer.jsp"/>

</body>
</html>
