<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Accounts — Admin Console — HariMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/responsive.css">
</head>
<body>

    <jsp:include page="includes/header.jsp"/>

    <div class="dashboard-container">
        <div class="dashboard-header">
            <div class="dashboard-title">
                <h2>User Account Management</h2>
                <p>Manage buyer and merchant accounts, roles, and security authorization.</p>
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
            <div class="data-card-header" style="flex-wrap: wrap; gap: 1rem;">
                <h4>All Accounts (${users.size()})</h4>
                <form action="${pageContext.request.contextPath}/admin/users" method="GET" style="display: flex; gap: 0.5rem;">
                    <input type="text" name="search" value="${searchKeyword}" placeholder="Search name or email..." class="form-control" style="width: 250px; padding: 0.4rem 0.75rem; font-size: 0.88rem;">
                    <button type="submit" class="btn-action-sm">Search</button>
                    <c:if test="${not empty searchKeyword}">
                        <a href="${pageContext.request.contextPath}/admin/users" class="btn-action-sm">Reset</a>
                    </c:if>
                </form>
            </div>
            <div class="data-table-wrapper">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>User ID</th>
                            <th>Full Name & Email</th>
                            <th>Phone</th>
                            <th>Joined</th>
                            <th>Role</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty users}">
                                <tr>
                                    <td colspan="7" style="text-align: center; padding: 3rem; color: var(--slate-400);">
                                        No users matching query.
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="u" items="${users}">
                                    <tr>
                                        <td style="font-family: monospace; font-weight: 700; color: var(--slate-500);">#${u.id}</td>
                                        <td>
                                            <div style="font-weight: 600; color: var(--navy-900);">${u.name}</div>
                                            <div style="font-size: 0.82rem; color: var(--slate-500);">${u.email}</div>
                                        </td>
                                        <td>${u.phone != null ? u.phone : '—'}</td>
                                        <td style="font-size: 0.82rem; color: var(--slate-500);">${u.createdAt}</td>
                                        <td>
                                            <form action="${pageContext.request.contextPath}/admin/users/role" method="POST" style="display: inline-flex; align-items: center; gap: 0.35rem;">
                                                <input type="hidden" name="userId" value="${u.id}">
                                                <select name="role" class="form-control" style="padding: 0.25rem 0.5rem; font-size: 0.82rem; width: auto;" onchange="this.form.submit()">
                                                    <option value="BUYER" ${u.role == 'BUYER' ? 'selected' : ''}>BUYER</option>
                                                    <option value="SELLER" ${u.role == 'SELLER' ? 'selected' : ''}>SELLER</option>
                                                    <option value="ADMIN" ${u.role == 'ADMIN' ? 'selected' : ''}>ADMIN</option>
                                                </select>
                                            </form>
                                        </td>
                                        <td>
                                            <span class="status-pill ${u.status == 'ACTIVE' ? 'active' : 'suspended'}">
                                                ${u.status}
                                            </span>
                                        </td>
                                        <td>
                                            <form action="${pageContext.request.contextPath}/admin/users/status" method="POST" style="display: inline;">
                                                <input type="hidden" name="userId" value="${u.id}">
                                                <input type="hidden" name="status" value="${u.status == 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'}">
                                                <button type="submit" class="btn-action-sm" style="font-size: 0.8rem;">
                                                    ${u.status == 'ACTIVE' ? 'Deactivate' : 'Activate'}
                                                </button>
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
