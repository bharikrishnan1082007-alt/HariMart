package com.harimart.service.impl;

import com.harimart.enums.PaymentMethod;
import com.harimart.enums.PaymentStatus;
import com.harimart.service.PaymentService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class MockPaymentService implements PaymentService {

    @Override
    public PaymentResult processPayment(PaymentMethod method, BigDecimal amount, String orderNumber) {
        if (method == PaymentMethod.COD) {
            return new PaymentResult(
                    true,
                    PaymentStatus.PENDING,
                    "COD-" + orderNumber,
                    "Cash on Delivery selected. Payment will be collected upon delivery."
            );
        }

        // Online Mock Payment Gateway simulation
        String mockTxnId = "HM_TXN_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new PaymentResult(
                true,
                PaymentStatus.COMPLETED,
                mockTxnId,
                "Online payment simulated successfully via HariMart Mock Gateway."
        );
    }
}
