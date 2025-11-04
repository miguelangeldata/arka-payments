package com.arka.payments.events;

import lombok.Getter;

import java.util.UUID;
@Getter
public class PaymentRejectedEvent {
    private String id= UUID.randomUUID().toString();
    private String userId;
    private String userEmail;

    public PaymentRejectedEvent(String userId, String userEmail) {
        this.userId = userId;
        this.userEmail = userEmail;
    }
}
