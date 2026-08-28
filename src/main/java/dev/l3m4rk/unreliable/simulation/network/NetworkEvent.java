package dev.l3m4rk.unreliable.simulation.network;

public sealed interface NetworkEvent
        permits MessageSent, MessageDelivered, MessageDropped {
}
