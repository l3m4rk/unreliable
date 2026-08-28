package dev.l3m4rk.unreliable.simulation;

import java.util.Objects;

public record SimulationLogEntry(
        SimTime time,
        Object event
) {

    public SimulationLogEntry {
        Objects.requireNonNull(time);
        Objects.requireNonNull(event);
    }
}
