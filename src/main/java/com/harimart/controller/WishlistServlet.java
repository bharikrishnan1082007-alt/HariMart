package com.harimart.controller;

import com.google.gson.Gson;
import com.harimart.dao.WishlistDAO;
import com.harimart.model.User;
import com.harimart.model.Wishlist;

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

@WebServlet(name = "WishlistServlet", urlPatterns = {"/wishlist", "/wishlist/*"})
public class WishlistServlet extends HttpServlet {

    private final WishlistDAO wishlistDAO = new WishlistDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login?returnUrl=" + req.getContextPath() + "/wishlist");
            return;
        }

        List<Wishlist> wishlist = wishlistDAO.findByUserId(user.getId());
        req.setAttribute("wishlist", wishlist);
        req.getRequestDispatcher("/WEB-INF/views/wishlist.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(req.getHeader("X-Requested-With"));

        if (user == null) {
            if (isAjax) {
                sendJsonResponse(resp, false, "Please log in to manage your wishlist.", Map.of("requireLogin", true));
            } else {
                resp.sendRedirect(req.getContextPath() + "/login");
            }
            return;
        }

        String path = req.getPathInfo();
        if (path == null) path = "";

        try {
            Long productId = Long.parseLong(req.getParameter("productId"));

            if ("/toggle".equals(path)) {
                boolean inWishlist = wishlistDAO.toggle(user.getId(), productId);
                int count = wishlistDAO.countByUserId(user.getId());
                if (isAjax) {
                    sendJsonResponse(resp, true, inWishlist ? "Added to wishlist!" : "Removed from wishlist.", 
                            Map.of("inWishlist", inWishlist, "count", count));
                } else {
                    resp.sendRedirect(req.getContextPath() + "/wishlist");
                }
                return;
            }

            if ("/remove".equals(path)) {
                wishlistDAO.remove(user.getId(), productId);
                int count = wishlistDAO.countByUserId(user.getId());
                if (isAjax) {
                    sendJsonResponse(resp, true, "Removed from wishlist.", Map.of("count", count));
                } else {
                    resp.sendRedirect(req.getContextPath() + "/wishlist");
                }
                return;
            }
        } catch (Exception e) {
            if (isAjax) {
                sendJsonResponse(resp, false, e.getMessage(), null);
            } else {
                resp.sendRedirect(req.getContextPath() + "/wishlist");
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
