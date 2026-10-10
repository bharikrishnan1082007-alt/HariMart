package com.harimart.controller;

import com.harimart.dao.AddressDAO;
import com.harimart.model.Address;
import com.harimart.model.CartItem;
import com.harimart.model.Order;
import com.harimart.model.Product;
import com.harimart.model.User;
import com.harimart.service.CartService;
import com.harimart.service.OrderService;
import com.harimart.service.ProductService;
import com.harimart.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "CheckoutServlet", urlPatterns = {"/checkout", "/checkout/*"})
public class CheckoutServlet extends HttpServlet {

    private final CartService cartService = new CartService();
    private final OrderService orderService = new OrderService();
    private final AddressDAO addressDAO = new AddressDAO();
    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        User user = (User) session.getAttribute("currentUser");

        if (user == null) {
            String returnUrl = req.getRequestURI();
            if (req.getQueryString() != null) returnUrl += "?" + req.getQueryString();
            resp.sendRedirect(req.getContextPath() + "/login?returnUrl=" + java.net.URLEncoder.encode(returnUrl, "UTF-8"));
            return;
        }

        // Direct Buy Now support
        String directProdParam = req.getParameter("buyNowProductId");
        if (directProdParam != null && !directProdParam.isEmpty()) {
            try {
                Long pId = Long.parseLong(directProdParam);
                int qty = 1;
                String qtyStr = req.getParameter("buyNowQuantity");
                if (qtyStr != null) qty = Integer.parseInt(qtyStr);
                Product product = productService.getProductById(pId);
                if (product != null) {
                    req.setAttribute("directProduct", product);
                    req.setAttribute("directQuantity", qty);
                }
            } catch (Exception ignored) {}
        }

        List<CartItem> cartItems = cartService.getCartItems(user.getId(), null);
        String couponCode = (String) session.getAttribute("appliedCoupon");
        CartService.CartTotals totals = cartService.calculateTotals(cartItems, couponCode);

        List<Address> addresses = addressDAO.findByUserId(user.getId());

        req.setAttribute("cartItems", cartItems);
        req.setAttribute("totals", totals);
        req.setAttribute("addresses", addresses);

        req.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        User user = (User) session.getAttribute("currentUser");

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            Long addressId = null;
            String addressIdStr = req.getParameter("addressId");
            if (addressIdStr != null && !addressIdStr.isEmpty() && !"new".equalsIgnoreCase(addressIdStr)) {
                addressId = Long.parseLong(addressIdStr);
            } else {
                // Create new delivery address
                String fullName = req.getParameter("fullName");
                String phone = req.getParameter("phone");
                String addressLine1 = req.getParameter("addressLine1");
                String addressLine2 = req.getParameter("addressLine2");
                String city = req.getParameter("city");
                String state = req.getParameter("state");
                String pincode = req.getParameter("pincode");

                if (!ValidationUtil.isNotEmpty(fullName) || !ValidationUtil.isValidPhone(phone)
                        || !ValidationUtil.isNotEmpty(addressLine1) || !ValidationUtil.isNotEmpty(pincode)) {
                    throw new IllegalArgumentException("Please provide all required delivery address fields.");
                }

                Address newAddr = new Address();
                newAddr.setUserId(user.getId());
                newAddr.setFullName(fullName.trim());
                newAddr.setPhone(phone.trim());
                newAddr.setAddressLine1(addressLine1.trim());
                newAddr.setAddressLine2(addressLine2 != null ? addressLine2.trim() : "");
                newAddr.setCity(city.trim());
                newAddr.setState(state.trim());
                newAddr.setPincode(pincode.trim());
                newAddr.setIsDefault(true);

                Address saved = addressDAO.create(newAddr);
                if (saved != null) {
                    addressId = saved.getId();
                } else {
                    throw new IllegalStateException("Failed to save delivery address.");
                }
            }

            String paymentMethod = req.getParameter("paymentMethod");
            if (paymentMethod == null || paymentMethod.isEmpty()) {
                paymentMethod = "COD";
            }

            String couponCode = (String) session.getAttribute("appliedCoupon");

            String directProdParam = req.getParameter("directProductId");
            Order order;

            if (directProdParam != null && !directProdParam.isEmpty()) {
                Long productId = Long.parseLong(directProdParam);
                int qty = Integer.parseInt(req.getParameter("directQuantity"));
                order = orderService.placeDirectBuyNowOrder(user.getId(), addressId, productId, qty, paymentMethod, couponCode);
            } else {
                order = orderService.placeOrder(user.getId(), addressId, paymentMethod, couponCode);
            }

            session.removeAttribute("appliedCoupon");
            resp.sendRedirect(req.getContextPath() + "/orders/success?orderNumber=" + order.getOrderNumber());

        } catch (Exception e) {
            req.setAttribute("errorMessage", e.getMessage());
            doGet(req, resp);
        }
    }
}
