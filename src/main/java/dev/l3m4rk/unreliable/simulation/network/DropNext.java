package dev.l3m4rk.unreliable.simulation.network;

import java.util.Objects;

public final class DropNext implements NetworkRule {

    private final Class<? extends Message> messageType;

    private boolean consumed;

    public DropNext(Class<? extends Message> messageType) {
        this.messageType = Objects.requireNonNull(messageType);
    }

    @Override
    public boolean shouldDrop(Envelope envelope) {
        if (consumed) {
            return false;
        }

        if (!messageType.isInstance(envelope.payload())) {
            return false;
        }

        consumed = true;
        return true;
    }
}
