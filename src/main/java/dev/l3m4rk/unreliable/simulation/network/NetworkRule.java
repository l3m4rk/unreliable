package dev.l3m4rk.unreliable.simulation.network;

public interface NetworkRule {
    boolean shouldDrop(Envelope envelope);
}
