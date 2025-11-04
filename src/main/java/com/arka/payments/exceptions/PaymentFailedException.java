package com.arka.payments.exceptions;

public class PaymentFailedException extends RuntimeException {
    public PaymentFailedException(String message, Exception e) {
        super(message);
    }
}
