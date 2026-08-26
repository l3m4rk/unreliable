package dev.l3m4rk.unreliable.simulation;

import dev.l3m4rk.unreliable.simulation.network.Message;

import java.time.Duration;

public interface SimulationContext {
    SimTime now();
    void send(NodeId destination, Message message);
    void schedule(Duration delay, Runnable action);
}
