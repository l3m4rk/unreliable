package dev.l3m4rk.unreliable.simulation.network;

public record MessageSent(Envelope envelope) implements NetworkEvent {
}
