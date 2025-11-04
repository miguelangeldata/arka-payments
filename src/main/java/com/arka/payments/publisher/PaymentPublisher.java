package com.arka.payments.publisher;

import com.arka.payments.events.PaymentAcceptedEvent;
import com.arka.payments.events.PaymentRejectedEvent;

public interface PaymentPublisher {
    void paymentAcceptedPublisher(PaymentAcceptedEvent event);
    void paymentRejectedEventPublisher(PaymentRejectedEvent event);
}
