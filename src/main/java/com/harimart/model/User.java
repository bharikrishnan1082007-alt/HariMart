package com.harimart.model;

import java.sql.Timestamp;

public class User {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String passwordHash;
    private String role; // 'BUYER', 'SELLER', 'ADMIN'
    private String status; // 'ACTIVE', 'INACTIVE'
    private Timestamp createdAt;

    public User() {}

    public User(Long id, String name, String email, String phone, String passwordHash, String role, String status, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public boolean isAdmin() { return "ADMIN".equalsIgnoreCase(role); }
    public boolean isSeller() { return "SELLER".equalsIgnoreCase(role); }
    public boolean isBuyer() { return "BUYER".equalsIgnoreCase(role); }
}
