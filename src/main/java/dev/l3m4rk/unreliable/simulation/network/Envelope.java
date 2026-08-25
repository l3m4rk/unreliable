package dev.l3m4rk.unreliable.simulation.network;

import dev.l3m4rk.unreliable.simulation.NodeId;

import java.util.Objects;

public record Envelope(
        NodeId source,
        NodeId destination,
        Message payload
) {
    public Envelope {
        Objects.requireNonNull(source, "Source cannot be null");
        Objects.requireNonNull(destination, "Destination cannot be null");
        Objects.requireNonNull(payload, "Payload cannot be null");
    }
}
