<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Your Addresses — HariMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/responsive.css">
</head>
<body>

    <jsp:include page="includes/header.jsp"/>

    <main class="container" style="padding-top: 2.5rem; padding-bottom: 5rem; max-width: 900px;">

        <!-- Breadcrumbs -->
        <div style="font-size: 0.85rem; color: var(--slate-500); margin-bottom: 1.5rem;">
            <a href="${pageContext.request.contextPath}/">Home</a> &gt; 
            <a href="${pageContext.request.contextPath}/profile">Account</a> &gt; 
            <span style="color: var(--slate-800); font-weight: 600;">Addresses</span>
        </div>

        <div style="display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 2rem;">
            <div>
                <h1 style="font-size: 1.85rem; font-weight: 800; color: var(--navy-800);">Your Delivery Addresses</h1>
                <p style="color: var(--slate-500); font-size: 0.95rem;">Manage delivery locations for 1-click doorstep delivery</p>
            </div>
            <button type="button" class="btn-primary" onclick="document.getElementById('addAddressCard').scrollIntoView({behavior: 'smooth'})" style="font-size: 0.9rem; padding: 0.65rem 1.25rem;">
                + Add Address
            </button>
        </div>

        <!-- Address Cards Grid -->
        <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 1.5rem; margin-bottom: 3.5rem;">
            <c:forEach var="addr" items="${addresses}">
                <div style="background-color: #ffffff; border-radius: var(--radius-xl); border: 2px solid ${addr.isDefault ? 'var(--navy-800)' : 'var(--slate-200)'}; padding: 1.5rem; display: flex; flex-direction: column; justify-content: space-between; box-shadow: var(--shadow-sm);">
                    <div>
                        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem;">
                            <strong style="font-size: 1.05rem; color: var(--navy-800);">${addr.fullName}</strong>
                            <c:if test="${addr.isDefault}">
                                <span style="background-color: var(--emerald-100); color: var(--emerald-600); font-size: 0.75rem; font-weight: 700; padding: 0.15rem 0.5rem; border-radius: 4px;">Default</span>
                            </c:if>
                        </div>
                        <div style="font-size: 0.9rem; color: var(--slate-600); line-height: 1.5;">
                            ${addr.addressLine1}<br>
                            <c:if test="${not empty addr.addressLine2}">${addr.addressLine2}<br></c:if>
                            ${addr.city}, ${addr.state} - ${addr.pincode}<br>
                            ${addr.country}
                        </div>
                        <div style="font-size: 0.85rem; color: var(--slate-500); margin-top: 0.75rem;">
                            Phone number: <strong>${addr.phone}</strong>
                        </div>
                    </div>

                    <div style="display: flex; gap: 0.75rem; border-top: 1px solid var(--slate-100); margin-top: 1.25rem; padding-top: 1rem;">
                        <c:if test="${!addr.isDefault}">
                            <form action="${pageContext.request.contextPath}/addresses/default" method="POST" style="margin: 0;">
                                <input type="hidden" name="addressId" value="${addr.id}">
                                <button type="submit" class="btn-action-sm">Set Default</button>
                            </form>
                        </c:if>
                        <form action="${pageContext.request.contextPath}/addresses/delete" method="POST" style="margin: 0;" onsubmit="return confirm('Delete this address?');">
                            <input type="hidden" name="addressId" value="${addr.id}">
                            <button type="submit" class="btn-action-sm" style="color: var(--rose-600);">Delete</button>
                        </form>
                    </div>
                </div>
            </c:forEach>
        </div>

        <!-- Add Address Form -->
        <div id="addAddressCard" style="background-color: #ffffff; border-radius: var(--radius-xl); border: 1px solid var(--slate-200); padding: 2rem; box-shadow: var(--shadow-sm);">
            <h3 style="font-size: 1.35rem; font-weight: 800; color: var(--navy-800); margin-bottom: 1.5rem;">Add a New Address</h3>

            <form action="${pageContext.request.contextPath}/addresses/add" method="POST">
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                    <div class="form-group">
                        <label class="form-label">Full Name</label>
                        <input type="text" name="fullName" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Mobile Number</label>
                        <input type="tel" name="phone" class="form-control" placeholder="10-digit mobile number" required>
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label">Flat, House no., Building, Company, Apartment</label>
                    <input type="text" name="addressLine1" class="form-control" required>
                </div>

                <div class="form-group">
                    <label class="form-label">Area, Colony, Street, Sector, Village (Optional)</label>
                    <input type="text" name="addressLine2" class="form-control">
                </div>

                <div style="display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 1rem;">
                    <div class="form-group">
                        <label class="form-label">Town / City</label>
                        <input type="text" name="city" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label">State</label>
                        <input type="text" name="state" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Pincode</label>
                        <input type="text" name="pincode" class="form-control" placeholder="6 digits [0-9]" required>
                    </div>
                </div>

                <div class="form-group" style="display: flex; align-items: center; gap: 0.5rem;">
                    <input type="checkbox" name="isDefault" value="true" id="isDefaultCheck">
                    <label for="isDefaultCheck" style="font-size: 0.9rem; font-weight: 500; cursor: pointer;">Make this my default address</label>
                </div>

                <button type="submit" class="btn-primary" style="padding: 0.75rem 1.75rem; font-size: 0.95rem;">
                    Save Delivery Address
                </button>
            </form>
        </div>

    </main>

    <jsp:include page="includes/footer.jsp"/>

</body>
</html>
