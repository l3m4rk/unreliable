package dev.l3m4rk.unreliable.simulation.network;

public record MessageDropped(Envelope envelope) implements NetworkEvent {
}
