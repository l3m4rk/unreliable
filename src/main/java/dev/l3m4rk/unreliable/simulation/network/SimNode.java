package dev.l3m4rk.unreliable.simulation.network;

import dev.l3m4rk.unreliable.simulation.NodeId;
import dev.l3m4rk.unreliable.simulation.SimulationContext;

public interface SimNode {
    NodeId id();

    void receive(Envelope envelope, SimulationContext context);
}
