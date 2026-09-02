package dev.l3m4rk.unreliable.scenarios.lostack.event;

import java.util.UUID;

public record PaymentTimedOut(
        UUID paymentId,
        int attempt
) implements LostAckEvent {
}
