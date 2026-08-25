package dev.l3m4rk.unreliable.simulation;

import java.util.Objects;

public record NodeId(String value) {
    public NodeId {
        Objects.requireNonNull(value);

        if (value.isBlank()) {
            throw new IllegalArgumentException("Node cannot be blank");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
