package com.arka.payments.resources;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Details of a successfully processed payment refund.")
public record PaymentRefund(
        @Schema(description = "ID of the user who received the refund.", example = "USER-456")
        String userId,

        @Schema(description = "The amount that was successfully refunded.", example = "50.00")
        Double amount) {
}
