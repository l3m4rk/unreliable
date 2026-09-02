package dev.l3m4rk.unreliable.scenarios.lostack.event;

import java.util.UUID;

public record PaymentCharged(
        UUID paymentId,
        UUID chargeId,
        long amountCents
) implements LostAckEvent {
}
