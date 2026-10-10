package com.harimart.controller;

import com.harimart.model.User;
import com.harimart.service.AuthService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "ProfileServlet", urlPatterns = {"/profile"})
public class ProfileServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User freshUser = authService.getUserById(user.getId());
        req.setAttribute("user", freshUser != null ? freshUser : user);
        req.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String name = req.getParameter("name");
        String phone = req.getParameter("phone");

        boolean ok = authService.updateProfile(user.getId(), name, phone);
        if (ok) {
            User updated = authService.getUserById(user.getId());
            session.setAttribute("currentUser", updated);
            req.setAttribute("successMessage", "Profile updated successfully!");
        } else {
            req.setAttribute("errorMessage", "Failed to update profile. Please verify your inputs.");
        }

        doGet(req, resp);
    }
}
