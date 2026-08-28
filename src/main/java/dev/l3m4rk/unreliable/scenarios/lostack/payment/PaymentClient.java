package dev.l3m4rk.unreliable.scenarios.lostack.payment;

import dev.l3m4rk.unreliable.simulation.NodeId;
import dev.l3m4rk.unreliable.simulation.SimulationContext;
import dev.l3m4rk.unreliable.simulation.network.Envelope;
import dev.l3m4rk.unreliable.simulation.network.SimNode;

import java.util.Objects;
import java.util.Optional;

public final class PaymentClient implements SimNode {
    private final NodeId id;

    private PaymentResponse response;

    public PaymentClient(NodeId id) {
        this.id = Objects.requireNonNull(id);
    }

    @Override
    public NodeId id() {
        return id;
    }

    @Override
    public void receive(Envelope envelope, SimulationContext context) {
        if (!(envelope.payload() instanceof PaymentResponse paymentResponse)) {
            throw new IllegalArgumentException("Unsupported message: " + envelope.payload());
        }

        response = paymentResponse;
    }

    public void pay(NodeId paymentService, PaymentRequest request, SimulationContext context) {
        context.send(paymentService, request);
    }

    public Optional<PaymentResponse> response() {
        return Optional.ofNullable(response);
    }
}
