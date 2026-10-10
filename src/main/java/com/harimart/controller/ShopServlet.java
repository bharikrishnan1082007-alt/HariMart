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
import java.util.List;

@WebServlet(name = "ShopServlet", urlPatterns = {"", "/shop"})
public class ShopServlet extends HttpServlet {

    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Category> categories = productService.getActiveCategories();
        List<Product> featured = productService.getFeaturedProducts(8);
        List<Product> newArrivals = productService.getNewArrivals(8);
        List<Product> bestsellers = productService.getBestsellers(8);

        req.setAttribute("categories", categories);
        req.setAttribute("featuredProducts", featured);
        req.setAttribute("newArrivals", newArrivals);
        req.setAttribute("bestsellers", bestsellers);

        req.getRequestDispatcher("/WEB-INF/views/shop.jsp").forward(req, resp);
    }
}
