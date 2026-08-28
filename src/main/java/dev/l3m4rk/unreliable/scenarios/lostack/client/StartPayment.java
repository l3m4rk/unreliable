package dev.l3m4rk.unreliable.scenarios.lostack.client;

import dev.l3m4rk.unreliable.simulation.network.Message;

import java.util.Objects;
import java.util.UUID;

public record StartPayment(
        UUID paymentId,
        String idempotencyKey,
        long amountCents
) implements Message {
    public StartPayment {
        Objects.requireNonNull(paymentId);
        Objects.requireNonNull(idempotencyKey);

        if (idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("idempotencyKey must not be blank");
        }

        if (amountCents <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }
    }
}
