package com.harimart.controller;

import com.harimart.dao.AddressDAO;
import com.harimart.model.Address;
import com.harimart.model.User;
import com.harimart.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "AddressServlet", urlPatterns = {"/addresses", "/addresses/*"})
public class AddressServlet extends HttpServlet {

    private final AddressDAO addressDAO = new AddressDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        List<Address> addresses = addressDAO.findByUserId(user.getId());
        req.setAttribute("addresses", addresses);
        req.getRequestDispatcher("/WEB-INF/views/addresses.jsp").forward(req, resp);
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
        if (path == null) path = "";

        switch (path) {
            case "/add": {
                String fullName = req.getParameter("fullName");
                String phone = req.getParameter("phone");
                String addressLine1 = req.getParameter("addressLine1");
                String addressLine2 = req.getParameter("addressLine2");
                String city = req.getParameter("city");
                String state = req.getParameter("state");
                String pincode = req.getParameter("pincode");
                boolean isDefault = "true".equalsIgnoreCase(req.getParameter("isDefault"));

                if (ValidationUtil.isNotEmpty(fullName) && ValidationUtil.isValidPhone(phone)
                        && ValidationUtil.isNotEmpty(addressLine1) && ValidationUtil.isNotEmpty(pincode)) {
                    Address a = new Address();
                    a.setUserId(user.getId());
                    a.setFullName(fullName.trim());
                    a.setPhone(phone.trim());
                    a.setAddressLine1(addressLine1.trim());
                    a.setAddressLine2(addressLine2 != null ? addressLine2.trim() : "");
                    a.setCity(city.trim());
                    a.setState(state.trim());
                    a.setPincode(pincode.trim());
                    a.setIsDefault(isDefault);
                    addressDAO.create(a);
                }
                break;
            }

            case "/default": {
                String idStr = req.getParameter("addressId");
                if (idStr != null) {
                    addressDAO.setDefault(user.getId(), Long.parseLong(idStr));
                }
                break;
            }

            case "/delete": {
                String idStr = req.getParameter("addressId");
                if (idStr != null) {
                    addressDAO.delete(Long.parseLong(idStr), user.getId());
                }
                break;
            }
        }

        resp.sendRedirect(req.getContextPath() + "/addresses");
    }
}
