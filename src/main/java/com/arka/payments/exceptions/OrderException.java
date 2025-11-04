package com.arka.payments.exceptions;

import feign.FeignException;

public class OrderException extends RuntimeException {
    public OrderException(String message, FeignException e) {
        super(message,e);
    }
}
