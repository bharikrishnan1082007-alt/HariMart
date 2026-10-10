package com.harimart.controller;

import com.google.gson.Gson;
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

@WebServlet(name = "SearchServlet", urlPatterns = {"/search", "/autocomplete"})
public class SearchServlet extends HttpServlet {

    private final ProductService productService = new ProductService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        // AJAX Autocomplete Suggestions
        if ("/autocomplete".equals(path)) {
            String q = req.getParameter("q");
            List<String> suggestions = productService.autocomplete(q, 8);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(gson.toJson(suggestions));
            return;
        }

        // Search Results Page
        String keyword = req.getParameter("q");
        String catParam = req.getParameter("category");
        String sort = req.getParameter("sort");
        String minPriceStr = req.getParameter("minPrice");
        String maxPriceStr = req.getParameter("maxPrice");
        String minRatingStr = req.getParameter("rating");
        boolean inStock = "true".equalsIgnoreCase(req.getParameter("inStock"));

        Long categoryId = null;
        BigDecimal minPrice = null;
        BigDecimal maxPrice = null;
        Double minRating = null;

        try {
            if (catParam != null && !catParam.isEmpty()) categoryId = Long.parseLong(catParam);
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

        List<Product> products = productService.searchProducts(keyword, categoryId, minPrice, maxPrice, 
                minRating, inStock, sort, page, pageSize);
        int totalCount = productService.countSearchResults(keyword, categoryId, minPrice, maxPrice, minRating, inStock);
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        req.setAttribute("keyword", keyword);
        req.setAttribute("products", products);
        req.setAttribute("totalCount", totalCount);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("currentSort", sort);
        req.setAttribute("selectedCategory", categoryId);
        req.setAttribute("categories", productService.getActiveCategories());

        req.getRequestDispatcher("/WEB-INF/views/search-results.jsp").forward(req, resp);
    }
}
