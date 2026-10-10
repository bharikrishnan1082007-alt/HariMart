package com.harimart.controller;

import com.harimart.model.Product;
import com.harimart.model.Review;
import com.harimart.model.User;
import com.harimart.service.ProductService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "ProductServlet", urlPatterns = {"/product"})
public class ProductServlet extends HttpServlet {

    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        String slugParam = req.getParameter("slug");

        Product product = null;
        if (idParam != null && !idParam.isEmpty()) {
            try {
                product = productService.getProductById(Long.parseLong(idParam));
            } catch (NumberFormatException ignored) {}
        } else if (slugParam != null && !slugParam.isEmpty()) {
            product = productService.getProductBySlug(slugParam);
        }

        if (product == null || !Boolean.TRUE.equals(product.getIsActive())) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Product not found or is currently inactive.");
            return;
        }

        List<Product> related = productService.getRelatedProducts(product.getId(), product.getCategoryId(), 4);
        List<Review> reviews = productService.getProductReviews(product.getId());

        req.setAttribute("product", product);
        req.setAttribute("relatedProducts", related);
        req.setAttribute("reviews", reviews);

        User currentUser = (User) req.getSession().getAttribute("currentUser");
        boolean canReview = currentUser != null;
        req.setAttribute("canReview", canReview);

        req.getRequestDispatcher("/WEB-INF/views/product-detail.jsp").forward(req, resp);
    }
}
