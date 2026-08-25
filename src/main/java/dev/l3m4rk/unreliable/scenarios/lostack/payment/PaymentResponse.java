package dev.l3m4rk.unreliable.scenarios.lostack.payment;

import java.util.Objects;
import java.util.UUID;

///
/// @param paymentId клиентская операция
/// @param chargeId фактическое списание
/// @param amountCents
public record PaymentResponse(
        UUID paymentId,
        UUID chargeId,
        long amountCents
) implements PaymentMessage {
    public PaymentResponse {
        Objects.requireNonNull(paymentId);
        Objects.requireNonNull(chargeId);

        if (amountCents <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }
    }
}
