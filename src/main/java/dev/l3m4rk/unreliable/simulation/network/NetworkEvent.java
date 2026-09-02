package dev.l3m4rk.unreliable.simulation.network;

import dev.l3m4rk.unreliable.simulation.SimulationEvent;

public sealed interface NetworkEvent
        extends SimulationEvent
        permits MessageSent, MessageDelivered, MessageDropped {
}
