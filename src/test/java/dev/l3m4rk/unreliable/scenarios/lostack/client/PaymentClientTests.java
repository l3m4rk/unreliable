package dev.l3m4rk.unreliable.scenarios.lostack.client;

import dev.l3m4rk.unreliable.scenarios.lostack.payment.PaymentService;
import dev.l3m4rk.unreliable.simulation.NodeId;
import dev.l3m4rk.unreliable.simulation.SimulationEngine;
import dev.l3m4rk.unreliable.simulation.network.SimulatedNetwork;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PaymentClientTests {

    @Test
    void completesPaymentTroughSimulatedNetwork() {
        var engine = new SimulationEngine();
        var network = new SimulatedNetwork(engine);

        var clientId = new NodeId("client");
        var paymentId = new NodeId("payment");

        var client = new PaymentClient(
                clientId,
                paymentId
        );

        var payment = new PaymentService(
                paymentId
        );

        network.register(client);
        network.register(payment);

        var requestId = UUID.fromString(
                "00000000-0000-0000-0000-000000000042"
        );

        network.send(
                new NodeId("scenario"),
                client.id(),
                new StartPayment(
                        requestId,
                        "payment-42",
                        1_000
                )
        );

        engine.step(); // StartPayment reaches client
        engine.step(); // PaymentRequest reaches payment service
        engine.step(); // PaymentResponse reaches client

        assertEquals(1, payment.chargeCount());
        assertEquals(1_000, payment.totalChargedCents());

        assertTrue(client.response().isPresent());

        var response = client.response().orElseThrow();

        assertEquals(requestId, response.paymentId());
        assertEquals(1_000, response.amountCents());
    }
}