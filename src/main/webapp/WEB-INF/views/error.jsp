<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Notice — HariMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/responsive.css">
</head>
<body>

    <jsp:include page="includes/header.jsp"/>

    <div style="min-height: 60vh; display: flex; align-items: center; justify-content: center; padding: 3rem 1.5rem;">
        <div style="background-color: #ffffff; border-radius: var(--radius-xl); border: 1px solid var(--slate-200); box-shadow: var(--shadow-md); padding: 3.5rem 2.5rem; max-width: 580px; width: 100%; text-align: center;">
            
            <c:set var="statusCode" value="${requestScope['javax.servlet.error.status_code'] != null ? requestScope['javax.servlet.error.status_code'] : 404}"/>
            <c:set var="statusMsg" value="${requestScope['javax.servlet.error.message']}"/>

            <div style="font-size: 3.5rem; margin-bottom: 1rem;">
                <c:choose>
                    <c:when test="${statusCode == 404}">🔍</c:when>
                    <c:when test="${statusCode == 403}">🔒</c:when>
                    <c:when test="${statusCode == 500}">⚠️</c:when>
                    <c:otherwise>🧭</c:otherwise>
                </c:choose>
            </div>

            <h1 style="font-size: 2.2rem; font-weight: 800; color: var(--navy-900); margin-bottom: 0.5rem;">
                ${statusCode}
                <c:choose>
                    <c:when test="${statusCode == 404}">Page Not Found</c:when>
                    <c:when test="${statusCode == 403}">Access Forbidden</c:when>
                    <c:when test="${statusCode == 500}">Server Notice</c:when>
                    <c:otherwise>Request Notice</c:otherwise>
                </c:choose>
            </h1>

            <p style="color: var(--slate-500); font-size: 1.05rem; line-height: 1.6; margin-bottom: 2rem;">
                <c:choose>
                    <c:when test="${not empty statusMsg}">
                        ${statusMsg}
                    </c:when>
                    <c:when test="${statusCode == 404}">
                        The page or product you were looking for doesn't exist, may have been moved, or is temporarily unavailable.
                    </c:when>
                    <c:when test="${statusCode == 403}">
                        You do not possess the required credentials or permissions to view this merchant/admin portal.
                    </c:when>
                    <c:otherwise>
                        We encountered an unexpected condition while processing your request. Please try again shortly.
                    </c:otherwise>
                </c:choose>
            </p>

            <div style="display: flex; gap: 1rem; justify-content: center; flex-wrap: wrap;">
                <a href="${pageContext.request.contextPath}/" class="btn-primary" style="padding: 0.75rem 1.75rem;">
                    🏠 Back to HariMart Home
                </a>
                <a href="${pageContext.request.contextPath}/shop" class="btn-secondary" style="padding: 0.75rem 1.5rem;">
                    🛍 Browse Store
                </a>
            </div>
        </div>
    </div>

    <jsp:include page="includes/footer.jsp"/>

</body>
</html>
