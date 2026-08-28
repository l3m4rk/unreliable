package dev.l3m4rk.unreliable.scenarios.lostack.client;

import dev.l3m4rk.unreliable.scenarios.lostack.payment.PaymentRequest;
import dev.l3m4rk.unreliable.scenarios.lostack.payment.PaymentResponse;
import dev.l3m4rk.unreliable.simulation.NodeId;
import dev.l3m4rk.unreliable.simulation.SimulationContext;
import dev.l3m4rk.unreliable.simulation.SimulationEngine;
import dev.l3m4rk.unreliable.simulation.network.Envelope;
import dev.l3m4rk.unreliable.simulation.network.SimNode;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

public final class PaymentClient implements SimNode {
    private final NodeId id;
    private final NodeId paymentService;
    private final Duration timeout;

    private PaymentRequest pendingRequest;
    private PaymentResponse response;
    private int attempts;

    public PaymentClient(NodeId id, NodeId paymentService, Duration timeout) {
        this.id = Objects.requireNonNull(id);
        this.paymentService = Objects.requireNonNull(paymentService);
        this.timeout = Objects.requireNonNull(timeout);

        if (timeout.isNegative() || timeout.isZero()) {
            throw new IllegalArgumentException("Timeout must be positive");
        }
    }

    @Override
    public NodeId id() {
        return id;
    }

    @Override
    public void receive(Envelope envelope, SimulationContext context) {
        switch (envelope.payload()) {
            case StartPayment command -> startPayment(command, context);
            case PaymentResponse paymentResponse -> completePayment(paymentResponse);
            default -> throw new IllegalArgumentException(
                    "Unsupported message: " + envelope.payload()
            );
        }
    }

    private void startPayment(StartPayment command, SimulationContext context) {
        response = null;
        attempts = 0;

        pendingRequest = new PaymentRequest(
                command.paymentId(),
                command.idempotencyKey(),
                command.amountCents()
        );

        sendAttempt(context);

        context.schedule(timeout, () -> retryIfNecessary(context));
    }

    private void sendAttempt(SimulationContext context) {
        attempts++;

        context.send(paymentService, pendingRequest);
    }

    private void retryIfNecessary(SimulationContext context) {
        if (response != null) {
            return;
        }

        if (attempts >= 2) {
            return;
        }

        sendAttempt(context);
    }

    private void completePayment(PaymentResponse response) {
        this.response = response;
        this.pendingRequest = null;
    }

    public void pay(NodeId paymentService, PaymentRequest request, SimulationContext context) {
        context.send(paymentService, request);
    }

    public Optional<PaymentResponse> response() {
        return Optional.ofNullable(response);
    }

    public int attempts() {
        return attempts;
    }
}
