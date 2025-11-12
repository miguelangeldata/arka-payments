package com.arka.payments.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data required to initiate a new payment transaction.")
public class PaymentRequest {
    @Schema(description = "Unique ID of the order associated with this payment.", example = "ORD-789012")
    private String orderId;
    @Schema(description = "The total amount to be charged for the transaction.", example = "150.99")
    private Double amount;
}
