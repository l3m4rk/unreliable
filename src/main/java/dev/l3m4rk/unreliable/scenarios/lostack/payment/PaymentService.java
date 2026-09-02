package dev.l3m4rk.unreliable.scenarios.lostack.payment;

import dev.l3m4rk.unreliable.scenarios.lostack.event.PaymentCharged;
import dev.l3m4rk.unreliable.simulation.NodeId;
import dev.l3m4rk.unreliable.simulation.SimulationContext;
import dev.l3m4rk.unreliable.simulation.network.Envelope;
import dev.l3m4rk.unreliable.simulation.network.SimNode;

import java.util.Objects;
import java.util.UUID;

public final class PaymentService implements SimNode {
    private final NodeId id;

    private long nextChargeNumber = 1;
    private long totalChargedCents;
    private int chargeCount;

    public PaymentService(NodeId id) {
        this.id = Objects.requireNonNull(id);
    }

    @Override
    public NodeId id() {
        return id;
    }

    @Override
    public void receive(Envelope envelope, SimulationContext context) {
        if (!(envelope.payload() instanceof PaymentRequest request)) {
            throw new IllegalArgumentException("Unsupported message: " + envelope.payload());
        }

        var chargeId = new UUID(0, nextChargeNumber++);

        totalChargedCents = Math.addExact(totalChargedCents, request.amountCents());

        chargeCount++;

        context.record(new PaymentCharged(request.paymentId(), chargeId, request.amountCents()));

        context.send(envelope.source(), new PaymentResponse(request.paymentId(), chargeId, request.amountCents()));
    }

    public long totalChargedCents() {
        return totalChargedCents;
    }

    public int chargeCount() {
        return chargeCount;
    }
}
