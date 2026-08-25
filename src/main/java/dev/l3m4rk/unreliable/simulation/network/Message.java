package dev.l3m4rk.unreliable.simulation.network;

import dev.l3m4rk.unreliable.simulation.NodeId;

import java.util.Objects;

public record Message(
        NodeId source,
        NodeId destination,
        Object payload
) {
    public Message {
        Objects.requireNonNull(source);
        Objects.requireNonNull(destination);
        Objects.requireNonNull(payload);
    }
}
