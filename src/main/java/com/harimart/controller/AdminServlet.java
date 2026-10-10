package com.harimart.controller;

import com.harimart.model.AnalyticsSummary;
import com.harimart.model.Category;
import com.harimart.model.Order;
import com.harimart.model.Product;
import com.harimart.model.Seller;
import com.harimart.model.User;
import com.harimart.service.AdminService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminServlet", urlPatterns = {"/admin", "/admin/*"})
public class AdminServlet extends HttpServlet {

    private final AdminService adminService = new AdminService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null || path.isEmpty()) path = "/dashboard";

        switch (path) {
            case "/dashboard": {
                AnalyticsSummary summary = adminService.getPlatformSummary();
                List<Order> orders = adminService.getAllOrders(8);
                req.setAttribute("analytics", summary);
                req.setAttribute("recentOrders", orders);
                req.getRequestDispatcher("/WEB-INF/views/admin-dashboard.jsp").forward(req, resp);
                break;
            }

            case "/users": {
                String search = req.getParameter("search");
                List<User> users = adminService.getAllUsers(search);
                req.setAttribute("users", users);
                req.setAttribute("searchKeyword", search);
                req.getRequestDispatcher("/WEB-INF/views/admin-users.jsp").forward(req, resp);
                break;
            }

            case "/sellers": {
                List<Seller> sellers = adminService.getAllSellers();
                req.setAttribute("sellers", sellers);
                req.getRequestDispatcher("/WEB-INF/views/admin-sellers.jsp").forward(req, resp);
                break;
            }

            case "/products": {
                List<Product> products = adminService.getAllProducts();
                req.setAttribute("products", products);
                req.getRequestDispatcher("/WEB-INF/views/admin-products.jsp").forward(req, resp);
                break;
            }

            case "/categories": {
                List<Category> categories = adminService.getAllCategories();
                req.setAttribute("categories", categories);
                req.getRequestDispatcher("/WEB-INF/views/admin-categories.jsp").forward(req, resp);
                break;
            }

            default:
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null) path = "";

        try {
            switch (path) {
                case "/users/role": {
                    Long userId = Long.parseLong(req.getParameter("userId"));
                    String role = req.getParameter("role");
                    adminService.updateUserRole(userId, role);
                    resp.sendRedirect(req.getContextPath() + "/admin/users?msg=User role updated.");
                    break;
                }

                case "/users/status": {
                    Long userId = Long.parseLong(req.getParameter("userId"));
                    String status = req.getParameter("status");
                    adminService.updateUserStatus(userId, status);
                    resp.sendRedirect(req.getContextPath() + "/admin/users?msg=User status updated.");
                    break;
                }

                case "/sellers/status": {
                    Long sellerId = Long.parseLong(req.getParameter("sellerId"));
                    String status = req.getParameter("status");
                    adminService.updateSellerStatus(sellerId, status);
                    resp.sendRedirect(req.getContextPath() + "/admin/sellers?msg=Seller status updated.");
                    break;
                }

                case "/products/toggle": {
                    Long productId = Long.parseLong(req.getParameter("productId"));
                    adminService.toggleProductActive(productId);
                    resp.sendRedirect(req.getContextPath() + "/admin/products?msg=Product status toggled.");
                    break;
                }

                case "/categories/save": {
                    String idStr = req.getParameter("id");
                    String name = req.getParameter("name");
                    String description = req.getParameter("description");
                    String imageUrl = req.getParameter("imageUrl");

                    Category c = new Category();
                    if (idStr != null && !idStr.isEmpty()) {
                        c.setId(Long.parseLong(idStr));
                    }
                    c.setName(name);
                    c.setDescription(description);
                    c.setImageUrl(imageUrl);
                    c.setStatus("ACTIVE");
                    adminService.saveCategory(c);
                    resp.sendRedirect(req.getContextPath() + "/admin/categories?msg=Category saved.");
                    break;
                }

                case "/categories/delete": {
                    Long catId = Long.parseLong(req.getParameter("id"));
                    adminService.deleteCategory(catId);
                    resp.sendRedirect(req.getContextPath() + "/admin/categories?msg=Category deleted.");
                    break;
                }

                default:
                    resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            }
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard?err=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }
}
