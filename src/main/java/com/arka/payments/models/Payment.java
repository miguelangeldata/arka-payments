package com.arka.payments.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "payments")
public class Payment {

    @Id
    private String id= UUID.randomUUID().toString();
    private String orderId;
    private String userId;
    private String userEmail;
    private LocalDateTime createAt=LocalDateTime.now();
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;
    private String currency;
    private Double amount;
    private PaymentStatus status=PaymentStatus.PENDING;

    public Payment(Double amount, String orderId) {
        this.amount = amount;
        this.orderId = orderId;

    }
    public void switchToCanceled(){
        this.status=PaymentStatus.CANCELED;
    }
    public void switchToAcceptedPendingOrderConfirmation(){
        this.status=PaymentStatus.ACCEPTED_PENDING_ORDER_CONFIRMATION;
    }
    public void switchToAccepted(){
        this.status=PaymentStatus.APPROVED;
    }
    public void switchToRejected(){
        this.status=PaymentStatus.DECLINED;
    }
    public void switchToRefunded(){
        this.status=PaymentStatus.REFUNDED;
    }
}
