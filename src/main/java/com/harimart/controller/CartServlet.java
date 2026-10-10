package com.harimart.controller;

import com.google.gson.Gson;
import com.harimart.model.CartItem;
import com.harimart.model.User;
import com.harimart.service.CartService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "CartServlet", urlPatterns = {"/cart", "/cart/*"})
public class CartServlet extends HttpServlet {

    private final CartService cartService = new CartService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        User user = (User) session.getAttribute("currentUser");
        Long userId = (user != null) ? user.getId() : null;
        String sessionId = session.getId();

        List<CartItem> cartItems = cartService.getCartItems(userId, sessionId);
        String couponCode = (String) session.getAttribute("appliedCoupon");
        CartService.CartTotals totals = cartService.calculateTotals(cartItems, couponCode);

        req.setAttribute("cartItems", cartItems);
        req.setAttribute("totals", totals);
        req.setAttribute("cartCount", cartService.getCartCount(userId, sessionId));

        req.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null) path = "";

        HttpSession session = req.getSession(true);
        User user = (User) session.getAttribute("currentUser");
        Long userId = (user != null) ? user.getId() : null;
        String sessionId = session.getId();

        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(req.getHeader("X-Requested-With"));

        try {
            switch (path) {
                case "/add": {
                    Long productId = Long.parseLong(req.getParameter("productId"));
                    int quantity = 1;
                    String qtyStr = req.getParameter("quantity");
                    if (qtyStr != null && !qtyStr.isEmpty()) {
                        quantity = Integer.parseInt(qtyStr);
                    }
                    cartService.addToCart(userId, sessionId, productId, quantity);
                    int count = cartService.getCartCount(userId, sessionId);

                    if (isAjax) {
                        sendJsonResponse(resp, true, "Product added to cart!", Map.of("cartCount", count));
                    } else {
                        resp.sendRedirect(req.getContextPath() + "/cart");
                    }
                    break;
                }

                case "/update": {
                    Long cartItemId = Long.parseLong(req.getParameter("cartItemId"));
                    int quantity = Integer.parseInt(req.getParameter("quantity"));
                    cartService.updateQuantity(cartItemId, quantity);

                    List<CartItem> cartItems = cartService.getCartItems(userId, sessionId);
                    String couponCode = (String) session.getAttribute("appliedCoupon");
                    CartService.CartTotals totals = cartService.calculateTotals(cartItems, couponCode);
                    int count = cartService.getCartCount(userId, sessionId);

                    if (isAjax) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("cartCount", count);
                        data.put("subtotal", totals.getFormattedSubtotal());
                        data.put("discount", totals.getFormattedDiscount());
                        data.put("tax", totals.getFormattedTax());
                        data.put("shipping", totals.getFormattedShipping());
                        data.put("total", totals.getFormattedTotal());
                        sendJsonResponse(resp, true, "Cart updated.", data);
                    } else {
                        resp.sendRedirect(req.getContextPath() + "/cart");
                    }
                    break;
                }

                case "/remove": {
                    Long cartItemId = Long.parseLong(req.getParameter("cartItemId"));
                    cartService.removeFromCart(cartItemId);
                    int count = cartService.getCartCount(userId, sessionId);

                    if (isAjax) {
                        sendJsonResponse(resp, true, "Item removed from cart.", Map.of("cartCount", count));
                    } else {
                        resp.sendRedirect(req.getContextPath() + "/cart");
                    }
                    break;
                }

                case "/clear": {
                    cartService.clearCart(userId, sessionId);
                    session.removeAttribute("appliedCoupon");
                    if (isAjax) {
                        sendJsonResponse(resp, true, "Cart cleared.", Map.of("cartCount", 0));
                    } else {
                        resp.sendRedirect(req.getContextPath() + "/cart");
                    }
                    break;
                }

                case "/apply-coupon": {
                    String coupon = req.getParameter("coupon");
                    if ("HARI10".equalsIgnoreCase(coupon)) {
                        session.setAttribute("appliedCoupon", "HARI10");
                        if (isAjax) {
                            sendJsonResponse(resp, true, "Coupon HARI10 applied successfully! 10% instant discount added.", null);
                        } else {
                            resp.sendRedirect(req.getContextPath() + "/cart");
                        }
                    } else {
                        session.removeAttribute("appliedCoupon");
                        if (isAjax) {
                            sendJsonResponse(resp, false, "Invalid coupon code. Try HARI10.", null);
                        } else {
                            req.setAttribute("couponError", "Invalid coupon code. Try HARI10.");
                            doGet(req, resp);
                        }
                    }
                    break;
                }

                default:
                    resp.sendRedirect(req.getContextPath() + "/cart");
            }
        } catch (Exception e) {
            if (isAjax) {
                sendJsonResponse(resp, false, e.getMessage(), null);
            } else {
                req.setAttribute("errorMessage", e.getMessage());
                doGet(req, resp);
            }
        }
    }

    private void sendJsonResponse(HttpServletResponse resp, boolean success, String message, Map<String, Object> extra) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        Map<String, Object> map = new HashMap<>();
        map.put("success", success);
        map.put("message", message);
        if (extra != null) {
            map.putAll(extra);
        }
        resp.getWriter().write(gson.toJson(map));
    }
}
