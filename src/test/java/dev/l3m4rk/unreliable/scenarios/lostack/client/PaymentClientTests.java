package dev.l3m4rk.unreliable.scenarios.lostack.client;

import dev.l3m4rk.unreliable.scenarios.lostack.event.LostAckEvent;
import dev.l3m4rk.unreliable.scenarios.lostack.event.PaymentCharged;
import dev.l3m4rk.unreliable.scenarios.lostack.event.PaymentRetried;
import dev.l3m4rk.unreliable.scenarios.lostack.event.PaymentTimedOut;
import dev.l3m4rk.unreliable.scenarios.lostack.payment.PaymentResponse;
import dev.l3m4rk.unreliable.scenarios.lostack.payment.PaymentService;
import dev.l3m4rk.unreliable.simulation.NodeId;
import dev.l3m4rk.unreliable.simulation.SimulationEngine;
import dev.l3m4rk.unreliable.simulation.SimulationLogEntry;
import dev.l3m4rk.unreliable.simulation.network.DropNext;
import dev.l3m4rk.unreliable.simulation.network.SimulatedNetwork;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
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
                paymentId,
                Duration.ofMillis(500)
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

    @Test
    void losesPaymentResponse() {
        var engine = new SimulationEngine();
        var network = new SimulatedNetwork(engine);

        var clientId = new NodeId("client");
        var paymentId = new NodeId("payment");

        var client = new PaymentClient(
                clientId,
                paymentId,
                Duration.ofMillis(500)
        );

        var payment = new PaymentService(
                paymentId
        );

        network.register(client);
        network.register(payment);

        network.addRule(
                new DropNext(PaymentResponse.class)
        );

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

        engine.step(); // StartPayment
        engine.step(); // PaymentRequest
        engine.step(); // PaymentResponse → DROPPED

        assertEquals(1, payment.chargeCount());
        assertEquals(1_000, payment.totalChargedCents());

        assertTrue(client.response().isEmpty());
    }

    @Test
    void doesNotRetryWhenResponseArrives() {
        var engine = new SimulationEngine();
        var network = new SimulatedNetwork(engine);

        var clientId = new NodeId("client");
        var paymentId = new NodeId("payment");

        var client = new PaymentClient(
                clientId,
                paymentId,
                Duration.ofMillis(500)
        );

        var payment = new PaymentService(paymentId);

        network.register(client);
        network.register(payment);

        network.send(
                new NodeId("scenario"),
                client.id(),
                new StartPayment(
                        UUID.fromString(
                                "00000000-0000-0000-0000-000000000042"
                        ),
                        "payment-42",
                        1_000
                )
        );

        engine.step(); // StartPayment
        engine.step(); // PaymentRequest
        engine.step(); // PaymentResponse

        engine.step(); // timeout at 500ms

        assertEquals(1, client.attempts());
        assertEquals(1, payment.chargeCount());
    }

    @Test
    void retriesWhenPaymentResponseIsLost() {
        var engine = new SimulationEngine();
        var network = new SimulatedNetwork(engine);

        var clientId = new NodeId("client");
        var paymentId = new NodeId("payment");

        var client = new PaymentClient(
                clientId,
                paymentId,
                Duration.ofMillis(500)
        );

        var payment = new PaymentService(paymentId);

        network.register(client);
        network.register(payment);

        network.addRule(
                new DropNext(PaymentResponse.class)
        );

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

        engine.step(); // StartPayment
        engine.step(); // attempt #1 reaches payment
        engine.step(); // response #1 is dropped

        assertEquals(1, payment.chargeCount());
        assertTrue(client.response().isEmpty());

        engine.step(); // 500ms timeout → retry
        engine.step(); // attempt #2 reaches payment
        engine.step(); // response #2 reaches client

        assertEquals(2, client.attempts());

        assertEquals(2, payment.chargeCount());
        assertEquals(2_000, payment.totalChargedCents());

        assertTrue(client.response().isPresent());

        var paymentEvents = engine.log()
                .stream()
                .map(SimulationLogEntry::event)
                .filter(LostAckEvent.class::isInstance)
                .map(LostAckEvent.class::cast)
                .toList();

        assertEquals(
                List.of(
                        new PaymentCharged(
                                requestId,
                                new UUID(0, 1),
                                1_000
                        ),
                        new PaymentTimedOut(
                                requestId,
                                1
                        ),
                        new PaymentRetried(
                                requestId,
                                2
                        ),
                        new PaymentCharged(
                                requestId,
                                new UUID(0, 2),
                                1_000
                        )
                ),
                paymentEvents
        );

    }
}
