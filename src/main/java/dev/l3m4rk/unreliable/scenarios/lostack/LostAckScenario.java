package dev.l3m4rk.unreliable.scenarios.lostack;

import dev.l3m4rk.unreliable.scenarios.lostack.client.PaymentClient;
import dev.l3m4rk.unreliable.scenarios.lostack.client.StartPayment;
import dev.l3m4rk.unreliable.scenarios.lostack.payment.PaymentResponse;
import dev.l3m4rk.unreliable.scenarios.lostack.payment.PaymentService;
import dev.l3m4rk.unreliable.simulation.NodeId;
import dev.l3m4rk.unreliable.simulation.SimTime;
import dev.l3m4rk.unreliable.simulation.SimulationEngine;
import dev.l3m4rk.unreliable.simulation.SimulationLogEntry;
import dev.l3m4rk.unreliable.simulation.network.DropNext;
import dev.l3m4rk.unreliable.simulation.network.SimulatedNetwork;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class LostAckScenario {
    private static final NodeId SCENARIO = new NodeId("scenario");
    private static final NodeId CLIENT = new NodeId("client");
    private static final NodeId PAYMENT = new NodeId("payment");

    private static final UUID PAYMENT_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000042"
    );

    private static final long AMOUNT_CENTS = 1_000;

    private final SimulationEngine engine = new SimulationEngine();
    private final SimulatedNetwork network = new SimulatedNetwork(engine);

    private final PaymentClient client = new PaymentClient(CLIENT, PAYMENT, Duration.ofMillis(500));
    private final PaymentService payment = new PaymentService(PAYMENT);

    private boolean started;

    public LostAckScenario() {
        network.register(client);
        network.register(payment);

        network.addRule(new DropNext(PaymentResponse.class));
    }

    public void start() {
        if (started) {
            throw new IllegalStateException("Scenario already started");
        }

        this.started = true;

        network.send(SCENARIO, CLIENT, new StartPayment(PAYMENT_ID, "payment-42", AMOUNT_CENTS));
    }

    public boolean step() {
        return engine.step();
    }

    public void runToCompletion() {
        while (step()) {
            // execute scheduled events
        }
    }

    public SimTime now() {
        return engine.now();
    }

    public List<SimulationLogEntry> log() {
        return engine.log();
    }

    public int attempts() {
        return client.attempts();
    }

    public long totalChargedCents() {
        return payment.totalChargedCents();
    }

    public int chargeCount() {
        return payment.chargeCount();
    }

    public Optional<PaymentResponse> response() {
        return client.response();
    }
}
