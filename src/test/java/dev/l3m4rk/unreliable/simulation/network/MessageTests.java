package dev.l3m4rk.unreliable.simulation.network;

import dev.l3m4rk.unreliable.simulation.NodeId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MessageTests {

    @Test
    void createsMessage() {
        var source = new NodeId("client");
        var destination = new NodeId("payment");

        var payload = "pay 10 EUR";

        var message = new Message(source, destination, payload);

        assertEquals(source, message.source());
        assertEquals(destination, message.destination());
        assertEquals(payload, message.payload());
    }

    @Test
    void rejectsNullPayload() {
        assertThrows(NullPointerException.class, () -> {
            new Message(new NodeId("client"), new NodeId("payment"), null);
        });
    }
}