package com.arka.payments.controllers;

import com.arka.payments.models.Payment;
import com.arka.payments.resources.MetricsOfPayments;
import com.arka.payments.resources.PaymentAmount;
import com.arka.payments.resources.PaymentRefund;
import com.arka.payments.resources.PaymentRequest;
import com.arka.payments.services.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Management", description = "API for processing, tracking, and managing payment transactions and refunds.")
public class PaymentController {
    private final PaymentService paymentService;

    @Operation(
            summary = "Initiate a new payment transaction",
            description = "Creates a new payment record and initiates the transaction process (e.g., PENDING status)."
    )
    @ApiResponse(responseCode = "201", description = "Transaction successfully initiated.")
    @PostMapping("/process")
    public ResponseEntity<String> processPayment(@RequestBody PaymentRequest request) {
        String transactionId = paymentService.processPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Transaction Successfully initiated. ID: "+transactionId);
    }
    @Operation(
            summary = "Accept/Complete a PENDING payment",
            description = "Updates the status of a pending transaction to COMPLETED/ACCEPTED and records the final amount."
    )
    @ApiResponse(responseCode = "200", description = "Payment accepted successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid request or transaction ID format.")
    @PutMapping("/accept/{transactionId}")
    public ResponseEntity<String> acceptPayment(
            @PathVariable String transactionId, @RequestBody PaymentAmount amount) {
        try {
            String updatedId = paymentService.acceptPayment(transactionId, amount);
            return ResponseEntity.ok("Payment accepted successfully. ID: " + updatedId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @Operation(
            summary = "Cancel an active payment transaction",
            description = "Cancels a payment transaction if it's in a cancellable status (e.g., PENDING)."
    )
    @ApiResponse(responseCode = "204", description = "Payment cancelled successfully.")
    @ApiResponse(responseCode = "404", description = "Transaction ID not found.")
    @ApiResponse(responseCode = "409", description = "Conflict: Transaction cannot be cancelled in its current state.")
    @PutMapping("/{transactionId}")
    public ResponseEntity<Void> cancelPayment(@PathVariable String transactionId) {
        try {
            paymentService.cancelPayment(transactionId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {

            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {

            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
    @PostMapping("/refunded/{orderId}")
    public ResponseEntity<PaymentRefund> refunded(@PathVariable("orderId")String orderId){
        PaymentRefund refund=paymentService.refundedPayment(orderId);
        return ResponseEntity.ok(refund);
    }
    @Operation(
            summary = "Process a refund for a specific order",
            description = "Initiates the refund process for a previously completed payment associated with an Order ID."
    )
    @ApiResponse(responseCode = "200", description = "Refund successfully initiated/completed.")
    @GetMapping("/validate/{transactionId}")
    public ResponseEntity<Boolean> validatePayment(@PathVariable String transactionId) {
        boolean valid = paymentService.validatePayment(transactionId);
        return ResponseEntity.ok(valid);
    }
    @Operation(
            summary = "Validate transaction status",
            description = "Checks if a given transaction ID corresponds to a valid (e.g., COMPLETED or ACCEPTED) payment."
    )
    @ApiResponse(responseCode = "200", description = "Returns true if payment is valid, false otherwise.")
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }
    @GetMapping("/metrics")
    public ResponseEntity<MetricsOfPayments> paymentsStats(){
       MetricsOfPayments  metrics=paymentService.metricsOfPayments();
        return ResponseEntity.ok(metrics);
    }
}