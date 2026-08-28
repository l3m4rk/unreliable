package dev.l3m4rk.unreliable.scenarios.lostack.payment;

import dev.l3m4rk.unreliable.simulation.NodeId;
import dev.l3m4rk.unreliable.simulation.SimulationContext;
import dev.l3m4rk.unreliable.simulation.SimulationEngine;
import dev.l3m4rk.unreliable.simulation.network.Envelope;
import dev.l3m4rk.unreliable.simulation.network.SimNode;
import dev.l3m4rk.unreliable.simulation.network.SimulatedNetwork;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentServiceTests {

    @Test
    void chargesPaymentAndReturnsResponse() {
        var engine = new SimulationEngine();
        var network = new SimulatedNetwork(engine);

        var payment = new PaymentService(new NodeId("payment"));

        var client = new RecordingNode(new NodeId("client"));

        network.register(payment);
        network.register(client);

        var request = new PaymentRequest(
                UUID.fromString("00000000-0000-0000-0000-000000000042"),
                "payment-42",
                1_000
        );

        network.send(client.id(), payment.id(), request);

        engine.step(); // request reaches payment
        engine.step(); // response reaches client

        assertThat(payment.chargeCount()).isEqualTo(1);
        assertThat(payment.totalChargedCents()).isEqualTo(1_000);

        assertThat(client.received().size()).isEqualTo(1);

        var response = (PaymentResponse) client.received().getFirst().payload();
        assertThat(response.paymentId()).isEqualTo(request.paymentId());
        assertThat(response.amountCents()).isEqualTo(1_000);
    }

    @Test
    void completesPaymentRoundTrip() {
        var engine = new SimulationEngine();
        var network = new SimulatedNetwork(engine);

        var client = new PaymentClient(new NodeId("client"));
        var payment = new PaymentService(new NodeId("payment"));

        network.register(payment);
        network.register(client);

        var request = new PaymentRequest(
                UUID.fromString("00000000-0000-0000-0000-000000000042"),
                "payment-42",
                1_000
        );

        client.pay(payment.id(), request, network.contextFor(client.id()));

        engine.step(); // request reaches payment
        engine.step(); // response reaches client

        assertThat(payment.chargeCount()).isEqualTo(1);
        assertThat(payment.totalChargedCents()).isEqualTo(1_000);

        var response = client.response().orElseThrow();

        assertThat(response.paymentId()).isEqualTo(request.paymentId());
        assertThat(response.amountCents()).isEqualTo(1_000);
    }

    private static final class RecordingNode implements SimNode {
        private final NodeId id;
        private final List<Envelope> received = new ArrayList<>();

        public RecordingNode(NodeId id) {
            this.id = id;
        }

        @Override
        public NodeId id() {
            return id;
        }

        @Override
        public void receive(Envelope envelope, SimulationContext context) {
            received.add(envelope);
        }

        List<Envelope> received() {
            return List.copyOf(received);
        }
    }
}