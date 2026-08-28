package dev.l3m4rk.unreliable.simulation.network;

public record MessageDelivered(Envelope envelope) implements NetworkEvent {
}
