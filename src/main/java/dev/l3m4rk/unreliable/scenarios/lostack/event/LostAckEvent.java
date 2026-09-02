package dev.l3m4rk.unreliable.scenarios.lostack.event;

import dev.l3m4rk.unreliable.simulation.SimulationEvent;

public sealed interface LostAckEvent
        extends SimulationEvent
        permits PaymentCharged, PaymentTimedOut, PaymentRetried {
}
