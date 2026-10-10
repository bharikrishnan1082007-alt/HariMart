package com.harimart.model;

import com.harimart.util.CurrencyUtil;

import java.math.BigDecimal;

public class AnalyticsSummary {
    private long totalUsers;
    private long totalSellers;
    private long totalProducts;
    private long totalOrders;
    private BigDecimal totalRevenue = BigDecimal.ZERO;
    private BigDecimal totalCommission = BigDecimal.ZERO;
    private BigDecimal sellerNetEarnings = BigDecimal.ZERO;
    private long pendingOrders;
    private long deliveredOrders;
    private long lowStockProducts;

    public AnalyticsSummary() {}

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public long getTotalSellers() { return totalSellers; }
    public void setTotalSellers(long totalSellers) { this.totalSellers = totalSellers; }

    public long getTotalProducts() { return totalProducts; }
    public void setTotalProducts(long totalProducts) { this.totalProducts = totalProducts; }

    public long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public BigDecimal getTotalCommission() { return totalCommission; }
    public void setTotalCommission(BigDecimal totalCommission) { this.totalCommission = totalCommission; }

    public BigDecimal getSellerNetEarnings() { return sellerNetEarnings; }
    public void setSellerNetEarnings(BigDecimal sellerNetEarnings) { this.sellerNetEarnings = sellerNetEarnings; }

    public long getPendingOrders() { return pendingOrders; }
    public void setPendingOrders(long pendingOrders) { this.pendingOrders = pendingOrders; }

    public long getDeliveredOrders() { return deliveredOrders; }
    public void setDeliveredOrders(long deliveredOrders) { this.deliveredOrders = deliveredOrders; }

    public long getLowStockProducts() { return lowStockProducts; }
    public void setLowStockProducts(long lowStockProducts) { this.lowStockProducts = lowStockProducts; }

    public String getFormattedRevenue() {
        return CurrencyUtil.formatINR(totalRevenue);
    }

    public String getFormattedCommission() {
        return CurrencyUtil.formatINR(totalCommission);
    }

    public String getFormattedSellerEarnings() {
        return CurrencyUtil.formatINR(sellerNetEarnings);
    }
}
