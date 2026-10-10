package com.harimart.controller;

import com.harimart.model.User;
import com.harimart.service.AuthService;
import com.harimart.service.CartService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "AuthServlet", urlPatterns = {"/login", "/register", "/logout"})
public class AuthServlet extends HttpServlet {

    private final AuthService authService = new AuthService();
    private final CartService cartService = new CartService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/logout".equals(path)) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        if ("/login".equals(path)) {
            req.setAttribute("returnUrl", req.getParameter("returnUrl"));
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
            return;
        }

        if ("/register".equals(path)) {
            req.setAttribute("role", req.getParameter("role"));
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/login".equals(path)) {
            String email = req.getParameter("email");
            String password = req.getParameter("password");
            String returnUrl = req.getParameter("returnUrl");

            User user = authService.login(email, password);
            if (user != null) {
                HttpSession session = req.getSession(true);
                String oldSessionId = session.getId();
                session.setAttribute("currentUser", user);

                // Migrate guest cart items to user
                cartService.migrateGuestCart(oldSessionId, user.getId());

                if (returnUrl != null && !returnUrl.isEmpty() && !returnUrl.contains("/login") && !returnUrl.contains("/register")) {
                    resp.sendRedirect(returnUrl);
                } else if (user.isAdmin()) {
                    resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
                } else if (user.isSeller()) {
                    resp.sendRedirect(req.getContextPath() + "/seller/dashboard");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/");
                }
            } else {
                req.setAttribute("errorMessage", "Invalid email address or password.");
                req.setAttribute("email", email);
                req.setAttribute("returnUrl", returnUrl);
                req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
            }
            return;
        }

        if ("/register".equals(path)) {
            String name = req.getParameter("name");
            String email = req.getParameter("email");
            String phone = req.getParameter("phone");
            String password = req.getParameter("password");
            String role = req.getParameter("role");

            try {
                User user = authService.register(name, email, phone, password, role);
                if (user != null) {
                    HttpSession session = req.getSession(true);
                    session.setAttribute("currentUser", user);

                    if (user.isSeller()) {
                        resp.sendRedirect(req.getContextPath() + "/seller/register");
                    } else {
                        resp.sendRedirect(req.getContextPath() + "/");
                    }
                } else {
                    req.setAttribute("errorMessage", "Registration failed. Please try again.");
                    req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
                }
            } catch (IllegalArgumentException ex) {
                req.setAttribute("errorMessage", ex.getMessage());
                req.setAttribute("name", name);
                req.setAttribute("email", email);
                req.setAttribute("phone", phone);
                req.setAttribute("role", role);
                req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
            }
        }
    }
}
