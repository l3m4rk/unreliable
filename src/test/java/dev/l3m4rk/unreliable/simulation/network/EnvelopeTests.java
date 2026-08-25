package dev.l3m4rk.unreliable.simulation.network;

import dev.l3m4rk.unreliable.simulation.NodeId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnvelopeTests {

    @Test
    void createsEnvelope() {
        var source = new NodeId("client");
        var destination = new NodeId("payment");
        var payload = new TestMessage("hello");

        var message = new Envelope(source, destination, payload);

        assertEquals(source, message.source());
        assertEquals(destination, message.destination());
        assertEquals(payload, message.payload());
    }

    @Test
    void rejectsNullSource() {
        assertThrows(NullPointerException.class, () -> {
            new Envelope(null, new NodeId("payment"), new TestMessage("hello"));
        });
    }

    @Test
    void rejectsNullDestination() {
        assertThrows(NullPointerException.class, () -> {
            new Envelope(new NodeId("client"), null, new TestMessage("hello"));
        });
    }
    
    @Test
    void rejectsNullPayload() {
        assertThrows(NullPointerException.class, () -> {
            new Envelope(new NodeId("client"), new NodeId("payment"), null);
        });
    }

    private record TestMessage(String value) implements Message {}
}