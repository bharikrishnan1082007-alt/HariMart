package com.harimart.dto;

import com.harimart.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public class OrderRequest {

    private Long addressId;
    private AddressDTO newAddress;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    private String couponCode;

    // Optional: direct buy-now item if not checking out from cart
    private Long directProductId;
    private Integer directQuantity;

    public OrderRequest() {
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public AddressDTO getNewAddress() {
        return newAddress;
    }

    public void setNewAddress(AddressDTO newAddress) {
        this.newAddress = newAddress;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public Long getDirectProductId() {
        return directProductId;
    }

    public void setDirectProductId(Long directProductId) {
        this.directProductId = directProductId;
    }

    public Integer getDirectQuantity() {
        return directQuantity;
    }

    public void setDirectQuantity(Integer directQuantity) {
        this.directQuantity = directQuantity;
    }
}
