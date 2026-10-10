package com.harimart.service;

import com.harimart.enums.PaymentMethod;
import com.harimart.enums.PaymentStatus;

import java.math.BigDecimal;

public interface PaymentService {

    PaymentResult processPayment(PaymentMethod method, BigDecimal amount, String orderNumber);

    class PaymentResult {
        private final boolean successful;
        private final PaymentStatus status;
        private final String transactionReference;
        private final String message;

        public PaymentResult(boolean successful, PaymentStatus status, String transactionReference, String message) {
            this.successful = successful;
            this.status = status;
            this.transactionReference = transactionReference;
            this.message = message;
        }

        public boolean isSuccessful() {
            return successful;
        }

        public PaymentStatus getStatus() {
            return status;
        }

        public String getTransactionReference() {
            return transactionReference;
        }

        public String getMessage() {
            return message;
        }
    }
}
