package dev.l3m4rk.unreliable.simulation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NodeIdTestTests {

    @Test
    void createsNodeId() {
        var id = new NodeId("payment");

        assertEquals("payment", id.value());
    }

    @Test
    void rejectsBlankNodeId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new NodeId("  ")
        );
    }
}