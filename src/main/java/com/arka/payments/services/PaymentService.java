package com.arka.payments.services;


import com.arka.payments.events.PaymentAcceptedEvent;
import com.arka.payments.exceptions.InvalidPaymentException;
import com.arka.payments.exceptions.OrderException;
import com.arka.payments.exceptions.PaymentFailedException;
import com.arka.payments.exceptions.PaymentNotFound;
import com.arka.payments.feign.OrderClient;
import com.arka.payments.mapper.PaymentMapper;
import com.arka.payments.models.Payment;
import com.arka.payments.models.PaymentStatus;
import com.arka.payments.publisher.PaymentPublisher;
import com.arka.payments.repository.PaymentJpaRepository;
import com.arka.payments.resources.PaymentAmount;
import com.arka.payments.resources.PaymentRefund;
import com.arka.payments.resources.PaymentRequest;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final static Logger log=Logger.getLogger(PaymentService.class.getName());
    private final PaymentJpaRepository repository;
    private final PaymentMapper mapper;
    private final OrderClient orderClient;
    private final PaymentPublisher paymentPublisher;

    @CircuitBreaker(name = "paymentCreationCB", fallbackMethod = "handlePaymentCreationFailure")
    @Retry(name = "paymentCreationRetry", fallbackMethod = "handlePaymentCreationFailure")
    public String processPayment(PaymentRequest request){
        Payment payment=mapper.requestToDomain(request);
        Payment saved = repository.save(payment);
        return saved.getId();
    }
    @Transactional
    public String acceptPayment(String transactionId,PaymentAmount amount){
        Payment payment =getById(transactionId);
        Double expectPayment=expectPaymentTotal(payment.getId());
        if (!expectPayment.equals(amount.amount())){
            payment.switchToRejected();
            repository.save(payment);
            throw new InvalidPaymentException(
                    "Expected amount (" + expectPayment + ") " +
                            "does not match actual amount (" + amount.amount() + ")");
        }
        updateCredential(payment,amount);
        payment.switchToAcceptedPendingOrderConfirmation();
        repository.save(payment);
        try {
            callOrderService(payment);
            payment.switchToAccepted();
            repository.save(payment);
            paymentAcceptedPublisher(payment.getUserId(), payment.getUserEmail());
        } catch (OrderException e) {
            log.warning("Order service error, payment pending confirmation. ID: "
                    + transactionId + ". Max retries exceeded.");

        }
        return payment.getId();
    }

    public void cancelPayment(String transactionId){
        Payment payment =repository.findById(transactionId)
                .orElseThrow(()-> new IllegalArgumentException("Payment not Found by Id"));
        payment.switchToCanceled();
        repository.save(payment);
    }
    public Boolean validatePayment(String transactionId){
        return repository.findById(transactionId)
                .map(p -> p.getStatus() == PaymentStatus.APPROVED)
                .orElse(false);
    }
    public List<Payment> getAllPayments() {
        return repository.findAll();
    }

    public PaymentRefund refundedPayment(String orderId){
        Payment payment=repository.getByOrderId(orderId);
        if (!payment.getStatus().equals(PaymentStatus.APPROVED)){
            throw new IllegalArgumentException("For Refunded must be the payment approved");
        }
        payment.switchToRefunded();
        return new  PaymentRefund(payment.getUserId(),payment.getAmount());

    }
    private Payment getById(String paymentId){
        return repository.findById(paymentId)
                .orElseThrow(()-> new PaymentNotFound("Payment not Found by Id"));
    }
    private Double expectPaymentTotal(String transactionId){
        Payment payment =repository.findById(transactionId)
                .orElseThrow(()-> new IllegalArgumentException("Payment not Found by Id"));
        return  payment.getAmount();
    }
    private void updateCredential(Payment payment,PaymentAmount amount){
        mapper.updatePaymentDetails(payment,amount);
        mapper.updateUserDetails(payment,amount);
    }
    private void paymentAcceptedPublisher(String userId,String userEmail){
        PaymentAcceptedEvent event =new PaymentAcceptedEvent(userId,userEmail);
        paymentPublisher.paymentAcceptedPublisher(event);
    }
    @Retry(name = "orderServiceBackoffRetry", fallbackMethod = "handleOrderServiceFailure")
    private void callOrderService(Payment payment) {
        log.info("Try to Notify order Service with retry. Payment Id: " + payment.getId());
        orderClient.acceptOrder(payment.getOrderId(), payment.getUserId());
        log.info("Notification Successfully: " + payment.getId());
    }
    private void handleOrderServiceFailure(Payment payment, FeignException e) {
        log.severe("Order Service failed after max retries. Payment stays in PENDING_CONFIRMATION. Payment ID: "
                + payment.getId() + ". Error: " + e.getMessage());
    }
    private String handlePaymentCreationFailure(PaymentRequest request, Exception e) {
        log.severe("Payment creation failed after resiliency patterns. Cause: "
                + e.getClass().getSimpleName() + " - " + e.getMessage());
        throw new PaymentFailedException("Payment initiation failed due to infrastructure error (CB/Retry failure).", e);
    }

}
