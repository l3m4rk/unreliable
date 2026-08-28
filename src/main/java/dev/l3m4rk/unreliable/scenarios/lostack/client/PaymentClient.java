package dev.l3m4rk.unreliable.scenarios.lostack.client;

import dev.l3m4rk.unreliable.scenarios.lostack.payment.PaymentRequest;
import dev.l3m4rk.unreliable.scenarios.lostack.payment.PaymentResponse;
import dev.l3m4rk.unreliable.simulation.NodeId;
import dev.l3m4rk.unreliable.simulation.SimulationContext;
import dev.l3m4rk.unreliable.simulation.network.Envelope;
import dev.l3m4rk.unreliable.simulation.network.SimNode;

import java.util.Objects;
import java.util.Optional;

public final class PaymentClient implements SimNode {
    private final NodeId id;
    private final NodeId paymentService;

    private PaymentResponse response;

    public PaymentClient(NodeId id, NodeId paymentService) {
        this.id = Objects.requireNonNull(id);
        this.paymentService = paymentService;
    }

    @Override
    public NodeId id() {
        return id;
    }

    @Override
    public void receive(Envelope envelope, SimulationContext context) {
        switch (envelope.payload()) {
            case StartPayment command -> startPayment(command, context);
            case PaymentResponse paymentResponse -> response = paymentResponse;
            default -> throw new IllegalArgumentException(
                    "Unsupported message: " + envelope.payload()
            );
        }
    }

    private void startPayment(StartPayment command, SimulationContext context) {
        context.send(
                paymentService,
                new PaymentRequest(
                        command.paymentId(),
                        command.idempotencyKey(),
                        command.amountCents()
                ));
    }

    public void pay(NodeId paymentService, PaymentRequest request, SimulationContext context) {
        context.send(paymentService, request);
    }

    public Optional<PaymentResponse> response() {
        return Optional.ofNullable(response);
    }
}
