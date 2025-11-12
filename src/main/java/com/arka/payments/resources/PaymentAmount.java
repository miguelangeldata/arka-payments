package com.arka.payments.resources;

import com.arka.payments.models.PaymentType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Complete payment details used for accepting and finalizing a transaction.")
public record PaymentAmount(
        @Schema(description = "ID of the user making the payment.", example = "USER-456")
        String userId,

        @Schema(description = "User's email address for receipt and notification.", example = "user.payments@arka.com")
        String userEmail,

        @Schema(description = "The final amount processed in the transaction.", example = "150.99")
        Double amount,

        @Schema(description = "The currency code used for the transaction.", example = "USD")
        String currency,

        @Schema(description = "The type/method of payment (e.g., 'CreditCard', 'PayPal').", example = "CreditCard")
        PaymentType paymentType) {
}
