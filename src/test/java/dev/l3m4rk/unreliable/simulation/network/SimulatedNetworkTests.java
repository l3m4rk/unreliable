package dev.l3m4rk.unreliable.simulation.network;

import dev.l3m4rk.unreliable.simulation.NodeId;
import dev.l3m4rk.unreliable.simulation.SimulationContext;
import dev.l3m4rk.unreliable.simulation.SimulationEngine;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SimulatedNetworkTests {

    @Test
    void deliversMessageToDestinationNode() {
        var engine = new SimulationEngine();
        var network = new SimulatedNetwork(engine);

        var received = new ArrayList<Envelope>();

        var payment = new TestNode(
                new NodeId("payment"),
                (envelope, context) -> received.add(envelope)
        );

        network.register(payment);

        TestMessage hello = new TestMessage("hello");
        network.send(new NodeId("client"), payment.id(), hello);

        engine.step();

        assertThat(received.size()).isEqualTo(1);

        var envelope = received.getFirst();

        assertThat(envelope.source()).isEqualTo(new NodeId("client"));
        assertThat(envelope.destination()).isEqualTo(payment.id());
        assertThat(envelope.payload()).isEqualTo(hello);
    }

    @Test
    void respectsNetworkLatencyKey() {
        var engine = new SimulationEngine();
        var network = new SimulatedNetwork(engine);

        final int latency = 50;
        network.latency(Duration.ofMillis(latency));

        NodeId payment = new NodeId("payment");
        network.register(new TestNode(
                payment,
                (envelope, context) -> {
                })
        );

        network.send(new NodeId("client"), payment, new TestMessage("hello"));

        engine.step();

        assertThat(engine.now().millis()).isEqualTo(latency);
    }

    @Test
    void rejectsDuplicateNodeRegistration() {
        var engine = new SimulationEngine();
        var network = new SimulatedNetwork(engine);

        var node = new TestNode(
                new NodeId("payment"),
                (envelope, context) -> {});

        network.register(node);

        assertThrows(IllegalArgumentException.class, () -> network.register(node));
    }

    @Test
    void failsWhenDestinationDoesNotExist() {
        var engine = new SimulationEngine();
        var network = new SimulatedNetwork(engine);

        network.send(new NodeId("client"), new NodeId("missing"), new TestMessage("hello"));

        assertThrows(IllegalStateException.class, engine::step);
    }

    @Test
    void dropsNextMatchingMessage() {
        var engine = new SimulationEngine();
        var network = new SimulatedNetwork(engine);

        var received = new ArrayList<Envelope>();

        var destination = new TestNode(
                new NodeId("destination"),
                ((envelope, context) -> received.add(envelope))
        );

        network.register(destination);

        network.addRule(new DropNext(TestMessage.class));

        network.send(new NodeId("source"), destination.id(), new TestMessage("first"));
        network.send(new NodeId("source"), destination.id(), new TestMessage("second"));

        engine.step();
        engine.step();

        assertThat(received.size()).isEqualTo(1);
        assertThat(received.getFirst().payload()).isEqualTo(new TestMessage("second"));
    }

    private record TestMessage(String value) implements Message {
    }

    private record TestNode(
            NodeId id,
            MessageHandler handler
    ) implements SimNode {
        @Override
        public void receive(Envelope envelope, SimulationContext context) {
            handler.handle(envelope, context);
        }
    }

    @FunctionalInterface
    private interface MessageHandler {
        void handle(Envelope envelope, SimulationContext context);
    }
}