package com.arka.payments.publisher;

import com.arka.payments.events.PaymentAcceptedEvent;
import com.arka.payments.events.PaymentRejectedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Slf4j
@Component
public class Publisher implements PaymentPublisher {

    private final StreamBridge streamBridge;
    @Override
    public void paymentAcceptedPublisher(PaymentAcceptedEvent event) {
        final String BINDING_NAME = "paymentAcceptedSupplier-out-0";

        boolean success = streamBridge.send(BINDING_NAME, event);

        if (success) {
            log.info(" Event Payment Accepted Publisher: {}", event.getId());
        } else {

            log.error(" Critical Error publishing event: {}", event.getId());

            throw new RuntimeException("Fatal Error.");
        }
    }

    @Override
    public void paymentRejectedEventPublisher(PaymentRejectedEvent event) {
        final String BINDING_NAME = "paymentRejectedSupplier-out-0";

        boolean success = streamBridge.send(BINDING_NAME, event);

        if (success) {
            log.warn(" Event Payment Rejected Publisher: {}", event.getId());
        } else {
            log.error(" Critical Error publishing REJECTION event: {}", event.getId());
            throw new RuntimeException("Fatal Error publishing rejection event.");
        }
    }

}
