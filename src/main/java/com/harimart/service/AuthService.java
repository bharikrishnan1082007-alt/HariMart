package com.harimart.service;

import com.harimart.dao.UserDAO;
import com.harimart.model.User;
import com.harimart.util.PasswordUtil;
import com.harimart.util.ValidationUtil;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    public User login(String email, String password) {
        if (!ValidationUtil.isNotEmpty(email) || !ValidationUtil.isNotEmpty(password)) {
            return null;
        }

        User user = userDAO.findByEmail(email.trim());
        if (user == null || !"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            return null;
        }

        if (PasswordUtil.checkPassword(password, user.getPasswordHash())) {
            return user;
        }
        return null;
    }

    public User register(String name, String email, String phone, String password, String role) throws IllegalArgumentException {
        if (!ValidationUtil.isNotEmpty(name)) {
            throw new IllegalArgumentException("Full name is required.");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Valid email address is required.");
        }
        if (!ValidationUtil.isValidPhone(phone)) {
            throw new IllegalArgumentException("Valid 10-digit phone number is required.");
        }
        if (!ValidationUtil.isNotEmpty(password) || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }

        if (userDAO.findByEmail(email) != null) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }

        String targetRole = "SELLER".equalsIgnoreCase(role) ? "SELLER" : "BUYER";

        User newUser = new User();
        newUser.setName(name.trim());
        newUser.setEmail(email.trim().toLowerCase());
        newUser.setPhone(phone.trim());
        newUser.setPasswordHash(PasswordUtil.hashPassword(password));
        newUser.setRole(targetRole);
        newUser.setStatus("ACTIVE");

        return userDAO.create(newUser);
    }

    public boolean updateProfile(Long userId, String name, String phone) {
        if (userId == null || !ValidationUtil.isNotEmpty(name)) return false;
        User user = userDAO.findById(userId);
        if (user == null) return false;

        user.setName(name.trim());
        if (ValidationUtil.isValidPhone(phone)) {
            user.setPhone(phone.trim());
        }
        return userDAO.update(user);
    }

    public User getUserById(Long userId) {
        return userDAO.findById(userId);
    }
}
