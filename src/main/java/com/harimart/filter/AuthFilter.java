package com.harimart.filter;

import com.harimart.model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "AuthFilter", urlPatterns = {"/profile/*", "/addresses/*", "/checkout/*", "/orders/*", "/wishlist/*", "/seller/*", "/admin/*"})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getServletPath();
        String pathInfo = req.getPathInfo();
        String fullPath = path + (pathInfo != null ? pathInfo : "");

        // Allow unauthenticated seller registration page
        if ("/seller/register".equals(fullPath)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            String returnUrl = fullPath;
            if (req.getQueryString() != null) {
                returnUrl += "?" + req.getQueryString();
            }
            res.sendRedirect(req.getContextPath() + "/login?returnUrl=" + java.net.URLEncoder.encode(returnUrl, "UTF-8"));
            return;
        }

        // Admin routes require ADMIN role
        if (fullPath.startsWith("/admin")) {
            if (!currentUser.isAdmin()) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Administrator privileges required.");
                return;
            }
        }

        // Seller routes require SELLER or ADMIN role
        if (fullPath.startsWith("/seller")) {
            if (!currentUser.isSeller() && !currentUser.isAdmin()) {
                res.sendRedirect(req.getContextPath() + "/seller/register");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
