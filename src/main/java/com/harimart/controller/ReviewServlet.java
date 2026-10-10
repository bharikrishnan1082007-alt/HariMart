package com.harimart.controller;

import com.harimart.dao.ReviewDAO;
import com.harimart.model.User;
import com.harimart.service.ProductService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "ReviewServlet", urlPatterns = {"/review", "/review/*"})
public class ReviewServlet extends HttpServlet {

    private final ProductService productService = new ProductService();
    private final ReviewDAO reviewDAO = new ReviewDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String path = req.getPathInfo();
        if (path == null) path = "";

        if ("/add".equals(path)) {
            try {
                Long productId = Long.parseLong(req.getParameter("productId"));
                int rating = Integer.parseInt(req.getParameter("rating"));
                String title = req.getParameter("title");
                String comment = req.getParameter("comment");

                boolean success = productService.addReview(productId, user.getId(), rating, title, comment);
                if (success) {
                    resp.sendRedirect(req.getContextPath() + "/product?id=" + productId + "&msg=Review submitted successfully.");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/product?id=" + productId + "&err=Unable to submit review. You may have already reviewed this item.");
                }
            } catch (Exception e) {
                resp.sendRedirect(req.getContextPath() + "/shop");
            }
            return;
        }

        if ("/delete".equals(path)) {
            String reviewIdStr = req.getParameter("reviewId");
            String productIdStr = req.getParameter("productId");
            if (reviewIdStr != null && (user.isAdmin())) {
                reviewDAO.delete(Long.parseLong(reviewIdStr));
            }
            if (productIdStr != null) {
                resp.sendRedirect(req.getContextPath() + "/product?id=" + productIdStr);
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            }
        }
    }
}
