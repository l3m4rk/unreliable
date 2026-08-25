package dev.l3m4rk.unreliable.scenarios.lostack.payment;

import java.util.Objects;
import java.util.UUID;

public record PaymentRequest(
        UUID paymentId,
        String idempotencyKey,
        long amountCents
) implements PaymentMessage {

    public PaymentRequest {
        Objects.requireNonNull(paymentId);
        Objects.requireNonNull(idempotencyKey);

        if (idempotencyKey.isBlank()) {
            throw  new IllegalArgumentException("Idempotency key cannot be blank");
        }

        if (amountCents <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }
    }
}
