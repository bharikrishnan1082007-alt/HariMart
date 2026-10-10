package com.harimart.controller;

import com.harimart.model.Category;
import com.harimart.model.Product;
import com.harimart.service.ProductService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet(name = "CategoryServlet", urlPatterns = {"/category"})
public class CategoryServlet extends HttpServlet {

    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String slugParam = req.getParameter("slug");
        String idParam = req.getParameter("id");

        Category category = null;
        if (slugParam != null && !slugParam.isEmpty()) {
            category = productService.getCategoryBySlug(slugParam);
        } else if (idParam != null && !idParam.isEmpty()) {
            try {
                category = productService.getCategoryById(Long.parseLong(idParam));
            } catch (NumberFormatException ignored) {}
        }

        if (category == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Category not found.");
            return;
        }

        String sort = req.getParameter("sort");
        String minPriceStr = req.getParameter("minPrice");
        String maxPriceStr = req.getParameter("maxPrice");
        String minRatingStr = req.getParameter("rating");
        boolean inStock = "true".equalsIgnoreCase(req.getParameter("inStock"));

        BigDecimal minPrice = null;
        BigDecimal maxPrice = null;
        Double minRating = null;

        try {
            if (minPriceStr != null && !minPriceStr.isEmpty()) minPrice = new BigDecimal(minPriceStr);
            if (maxPriceStr != null && !maxPriceStr.isEmpty()) maxPrice = new BigDecimal(maxPriceStr);
            if (minRatingStr != null && !minRatingStr.isEmpty()) minRating = Double.parseDouble(minRatingStr);
        } catch (Exception ignored) {}

        int page = 1;
        int pageSize = 12;
        String pageStr = req.getParameter("page");
        if (pageStr != null) {
            try { page = Math.max(1, Integer.parseInt(pageStr)); } catch (Exception ignored) {}
        }

        List<Product> products = productService.searchProducts(null, category.getId(), minPrice, maxPrice, 
                minRating, inStock, sort, page, pageSize);
        int totalCount = productService.countSearchResults(null, category.getId(), minPrice, maxPrice, minRating, inStock);
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        req.setAttribute("category", category);
        req.setAttribute("products", products);
        req.setAttribute("totalCount", totalCount);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("currentSort", sort);
        req.setAttribute("categories", productService.getActiveCategories());

        req.getRequestDispatcher("/WEB-INF/views/category.jsp").forward(req, resp);
    }
}
