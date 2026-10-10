package com.harimart.service;

import com.harimart.dao.OrderDAO;
import com.harimart.dao.ProductDAO;
import com.harimart.dao.SellerDAO;
import com.harimart.dao.UserDAO;
import com.harimart.model.AnalyticsSummary;
import com.harimart.model.OrderItem;
import com.harimart.model.Product;
import com.harimart.model.Seller;
import com.harimart.util.ValidationUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class SellerService {

    private final SellerDAO sellerDAO = new SellerDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final UserDAO userDAO = new UserDAO();

    public Seller registerSeller(Long userId, String storeName, String businessEmail, String phone, 
                                 String address, String taxId) throws IllegalArgumentException {
        if (userId == null || !ValidationUtil.isNotEmpty(storeName) || !ValidationUtil.isValidEmail(businessEmail)) {
            throw new IllegalArgumentException("Store name and valid business email are required.");
        }

        Seller existing = sellerDAO.findByUserId(userId);
        if (existing != null) {
            throw new IllegalArgumentException("You have already applied or registered as a seller.");
        }

        Seller seller = new Seller();
        seller.setUserId(userId);
        seller.setStoreName(storeName.trim());
        seller.setBusinessEmail(businessEmail.trim().toLowerCase());
        seller.setPhone(phone != null ? phone.trim() : "");
        seller.setAddress(address != null ? address.trim() : "");
        seller.setTaxId(taxId != null ? taxId.trim() : "");
        seller.setStatus("APPROVED"); // Auto-approve demo onboarding for smooth review
        seller.setCommissionRate(BigDecimal.valueOf(10.00));

        Seller created = sellerDAO.create(seller);
        if (created != null) {
            userDAO.updateRole(userId, "SELLER");
        }
        return created;
    }

    public Seller getSellerByUserId(Long userId) {
        return sellerDAO.findByUserId(userId);
    }

    public Seller getSellerById(Long id) {
        return sellerDAO.findById(id);
    }

    public List<Product> getSellerProducts(Long sellerId) {
        return productDAO.findBySeller(sellerId);
    }

    public Product saveProduct(Product product, Long sellerId) throws IllegalArgumentException {
        if (product == null || sellerId == null) {
            throw new IllegalArgumentException("Product data cannot be null.");
        }
        if (!ValidationUtil.isNotEmpty(product.getName()) || product.getPrice() == null) {
            throw new IllegalArgumentException("Product name and price are required.");
        }

        product.setSellerId(sellerId);

        // Auto-generate slug if missing
        if (product.getSlug() == null || product.getSlug().trim().isEmpty()) {
            String baseSlug = product.getName().toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
            product.setSlug(baseSlug + "-" + System.currentTimeMillis() % 10000);
        }

        if (product.getId() != null && product.getId() > 0) {
            // Update
            Product existing = productDAO.findById(product.getId());
            if (existing != null && existing.getSellerId().equals(sellerId)) {
                productDAO.update(product);
                return productDAO.findById(product.getId());
            } else {
                throw new SecurityException("Unauthorized product modification.");
            }
        } else {
            // Create
            return productDAO.create(product);
        }
    }

    public boolean deleteProduct(Long productId, Long sellerId) {
        Product p = productDAO.findById(productId);
        if (p != null && p.getSellerId().equals(sellerId)) {
            return productDAO.delete(productId);
        }
        return false;
    }

    public boolean toggleProductStock(Long productId, Long sellerId) {
        Product p = productDAO.findById(productId);
        if (p != null && p.getSellerId().equals(sellerId)) {
            return productDAO.toggleActive(productId);
        }
        return false;
    }

    public List<OrderItem> getSellerOrderItems(Long sellerId) {
        return orderDAO.findOrderItemsBySeller(sellerId);
    }

    public boolean updateOrderItemStatus(Long orderItemId, Long sellerId, String status) {
        return orderDAO.updateOrderItemStatus(orderItemId, sellerId, status);
    }

    public AnalyticsSummary getSellerAnalytics(Long sellerId) {
        AnalyticsSummary summary = new AnalyticsSummary();
        List<Product> products = productDAO.findBySeller(sellerId);
        List<OrderItem> items = orderDAO.findOrderItemsBySeller(sellerId);

        summary.setTotalProducts(products.size());
        summary.setTotalOrders(items.size());

        BigDecimal grossRevenue = BigDecimal.ZERO;
        long delivered = 0;
        long pending = 0;

        for (OrderItem item : items) {
            if (!"CANCELLED".equalsIgnoreCase(item.getFulfillmentStatus())) {
                grossRevenue = grossRevenue.add(item.getTotalPrice());
            }
            if ("DELIVERED".equalsIgnoreCase(item.getFulfillmentStatus())) {
                delivered++;
            } else if (!"CANCELLED".equalsIgnoreCase(item.getFulfillmentStatus())) {
                pending++;
            }
        }

        Seller seller = sellerDAO.findById(sellerId);
        BigDecimal commissionRate = (seller != null && seller.getCommissionRate() != null) 
                ? seller.getCommissionRate() 
                : BigDecimal.valueOf(10.00);

        BigDecimal commission = grossRevenue.multiply(commissionRate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        BigDecimal netEarnings = grossRevenue.subtract(commission);

        summary.setTotalRevenue(grossRevenue);
        summary.setTotalCommission(commission);
        summary.setSellerNetEarnings(netEarnings);
        summary.setDeliveredOrders(delivered);
        summary.setPendingOrders(pending);

        long lowStock = products.stream().filter(p -> p.getStockQuantity() <= 5).count();
        summary.setLowStockProducts(lowStock);

        return summary;
    }
}
