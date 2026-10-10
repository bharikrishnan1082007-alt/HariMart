package com.harimart.controller;

import com.harimart.model.*;
import com.harimart.service.ProductService;
import com.harimart.service.SellerService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet(name = "SellerServlet", urlPatterns = {"/seller", "/seller/*"})
public class SellerServlet extends HttpServlet {

    private final SellerService sellerService = new SellerService();
    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null || path.isEmpty()) path = "/dashboard";

        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;

        // Seller Onboarding
        if ("/register".equals(path)) {
            if (user == null) {
                resp.sendRedirect(req.getContextPath() + "/login?returnUrl=" + req.getContextPath() + "/seller/register");
                return;
            }
            Seller existing = sellerService.getSellerByUserId(user.getId());
            if (existing != null) {
                resp.sendRedirect(req.getContextPath() + "/seller/dashboard");
                return;
            }
            req.getRequestDispatcher("/WEB-INF/views/seller-register.jsp").forward(req, resp);
            return;
        }

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Seller seller = sellerService.getSellerByUserId(user.getId());
        if (seller == null && !user.isAdmin()) {
            resp.sendRedirect(req.getContextPath() + "/seller/register");
            return;
        }

        Long sellerId = (seller != null) ? seller.getId() : 1L; // Fallback for admin preview
        req.setAttribute("seller", seller);

        switch (path) {
            case "/dashboard": {
                AnalyticsSummary summary = sellerService.getSellerAnalytics(sellerId);
                List<Product> products = sellerService.getSellerProducts(sellerId);
                List<OrderItem> orders = sellerService.getSellerOrderItems(sellerId);

                req.setAttribute("analytics", summary);
                req.setAttribute("products", products);
                req.setAttribute("recentOrders", orders.size() > 5 ? orders.subList(0, 5) : orders);
                req.getRequestDispatcher("/WEB-INF/views/seller-dashboard.jsp").forward(req, resp);
                break;
            }

            case "/products": {
                List<Product> products = sellerService.getSellerProducts(sellerId);
                req.setAttribute("products", products);
                req.getRequestDispatcher("/WEB-INF/views/seller-products.jsp").forward(req, resp);
                break;
            }

            case "/products/add": {
                req.setAttribute("categories", productService.getActiveCategories());
                req.setAttribute("product", new Product());
                req.getRequestDispatcher("/WEB-INF/views/seller-product-form.jsp").forward(req, resp);
                break;
            }

            case "/products/edit": {
                String idStr = req.getParameter("id");
                Product product = (idStr != null) ? productService.getProductById(Long.parseLong(idStr)) : null;
                if (product == null || (!product.getSellerId().equals(sellerId) && !user.isAdmin())) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Product not found.");
                    return;
                }
                req.setAttribute("categories", productService.getActiveCategories());
                req.setAttribute("product", product);
                req.getRequestDispatcher("/WEB-INF/views/seller-product-form.jsp").forward(req, resp);
                break;
            }

            case "/orders": {
                List<OrderItem> orders = sellerService.getSellerOrderItems(sellerId);
                req.setAttribute("orders", orders);
                req.getRequestDispatcher("/WEB-INF/views/seller-orders.jsp").forward(req, resp);
                break;
            }

            case "/analytics": {
                AnalyticsSummary summary = sellerService.getSellerAnalytics(sellerId);
                req.setAttribute("analytics", summary);
                req.getRequestDispatcher("/WEB-INF/views/seller-analytics.jsp").forward(req, resp);
                break;
            }

            default:
                resp.sendRedirect(req.getContextPath() + "/seller/dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null) path = "";

        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            if ("/register".equals(path)) {
                String storeName = req.getParameter("storeName");
                String businessEmail = req.getParameter("businessEmail");
                String phone = req.getParameter("phone");
                String address = req.getParameter("address");
                String taxId = req.getParameter("taxId");

                Seller seller = sellerService.registerSeller(user.getId(), storeName, businessEmail, phone, address, taxId);
                user.setRole("SELLER");
                session.setAttribute("currentUser", user);
                resp.sendRedirect(req.getContextPath() + "/seller/dashboard?msg=Welcome to HariMart Seller Central!");
                return;
            }

            Seller seller = sellerService.getSellerByUserId(user.getId());
            Long sellerId = (seller != null) ? seller.getId() : 1L;

            switch (path) {
                case "/products/save": {
                    String idStr = req.getParameter("id");
                    String name = req.getParameter("name");
                    String categoryIdStr = req.getParameter("categoryId");
                    String priceStr = req.getParameter("price");
                    String discountStr = req.getParameter("discountPrice");
                    String stockStr = req.getParameter("stockQuantity");
                    String brand = req.getParameter("brand");
                    String imageUrl = req.getParameter("imageUrl");
                    String description = req.getParameter("description");
                    boolean isFeatured = "true".equalsIgnoreCase(req.getParameter("isFeatured"));
                    boolean isActive = "true".equalsIgnoreCase(req.getParameter("isActive"));

                    Product p = new Product();
                    if (idStr != null && !idStr.isEmpty()) {
                        p.setId(Long.parseLong(idStr));
                    }
                    p.setName(name);
                    p.setCategoryId(Long.parseLong(categoryIdStr));
                    p.setPrice(new BigDecimal(priceStr));
                    p.setDiscountPrice(discountStr != null && !discountStr.isEmpty() ? new BigDecimal(discountStr) : BigDecimal.ZERO);
                    p.setStockQuantity(Integer.parseInt(stockStr));
                    p.setBrand(brand);
                    p.setImageUrl(imageUrl);
                    p.setDescription(description);
                    p.setIsFeatured(isFeatured);
                    p.setIsActive(isActive);

                    sellerService.saveProduct(p, sellerId);
                    resp.sendRedirect(req.getContextPath() + "/seller/products?msg=Product saved successfully.");
                    break;
                }

                case "/products/toggle": {
                    Long productId = Long.parseLong(req.getParameter("productId"));
                    sellerService.toggleProductStock(productId, sellerId);
                    resp.sendRedirect(req.getContextPath() + "/seller/products");
                    break;
                }

                case "/products/delete": {
                    Long productId = Long.parseLong(req.getParameter("productId"));
                    sellerService.deleteProduct(productId, sellerId);
                    resp.sendRedirect(req.getContextPath() + "/seller/products?msg=Product deleted.");
                    break;
                }

                case "/orders/status": {
                    Long orderItemId = Long.parseLong(req.getParameter("orderItemId"));
                    String status = req.getParameter("status");
                    sellerService.updateOrderItemStatus(orderItemId, sellerId, status);
                    resp.sendRedirect(req.getContextPath() + "/seller/orders?msg=Order status updated.");
                    break;
                }

                default:
                    resp.sendRedirect(req.getContextPath() + "/seller/dashboard");
            }
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/seller/dashboard?err=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }
}
