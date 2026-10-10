package com.harimart.controller;

import com.harimart.model.Order;
import com.harimart.model.User;
import com.harimart.service.OrderService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "OrderServlet", urlPatterns = {"/orders", "/orders/*"})
public class OrderServlet extends HttpServlet {

    private final OrderService orderService = new OrderService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String path = req.getPathInfo();
        if (path == null) path = "";

        switch (path) {
            case "/success": {
                String orderNumber = req.getParameter("orderNumber");
                Order order = orderService.getOrderByNumber(orderNumber);
                req.setAttribute("order", order);
                req.getRequestDispatcher("/WEB-INF/views/order-success.jsp").forward(req, resp);
                return;
            }

            case "/detail": {
                String orderIdStr = req.getParameter("id");
                String orderNumber = req.getParameter("orderNumber");
                Order order = null;
                if (orderIdStr != null && !orderIdStr.isEmpty()) {
                    order = orderService.getOrderById(Long.parseLong(orderIdStr));
                } else if (orderNumber != null && !orderNumber.isEmpty()) {
                    order = orderService.getOrderByNumber(orderNumber);
                }

                if (order == null || (!order.getUserId().equals(user.getId()) && !user.isAdmin())) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found or unauthorized.");
                    return;
                }

                req.setAttribute("order", order);
                req.getRequestDispatcher("/WEB-INF/views/order-detail.jsp").forward(req, resp);
                return;
            }

            case "/invoice": {
                String orderIdStr = req.getParameter("id");
                Order order = (orderIdStr != null) ? orderService.getOrderById(Long.parseLong(orderIdStr)) : null;
                if (order == null || (!order.getUserId().equals(user.getId()) && !user.isAdmin())) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found.");
                    return;
                }
                req.setAttribute("order", order);
                req.setAttribute("isInvoice", true);
                req.getRequestDispatcher("/WEB-INF/views/order-detail.jsp").forward(req, resp);
                return;
            }

            default: {
                List<Order> orders = orderService.getUserOrders(user.getId());
                req.setAttribute("orders", orders);
                req.getRequestDispatcher("/WEB-INF/views/order-history.jsp").forward(req, resp);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String path = req.getPathInfo();
        if ("/cancel".equals(path)) {
            String orderIdStr = req.getParameter("orderId");
            if (orderIdStr != null) {
                Long orderId = Long.parseLong(orderIdStr);
                boolean cancelled = orderService.cancelOrder(orderId, user.getId());
                if (cancelled) {
                    resp.sendRedirect(req.getContextPath() + "/orders/detail?id=" + orderId + "&msg=Order cancelled successfully.");
                    return;
                } else {
                    resp.sendRedirect(req.getContextPath() + "/orders/detail?id=" + orderId + "&err=Unable to cancel this order.");
                    return;
                }
            }
        }

        resp.sendRedirect(req.getContextPath() + "/orders");
    }
}
