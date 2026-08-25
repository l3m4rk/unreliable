package dev.l3m4rk.unreliable.scenarios.lostack.payment;

import dev.l3m4rk.unreliable.simulation.network.Message;

public sealed interface PaymentMessage extends Message permits PaymentRequest, PaymentResponse {
}
