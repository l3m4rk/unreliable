package dev.l3m4rk.unreliable.simulation;

import java.util.Objects;

record ScheduledEvent(
        SimTime time,
        long sequence,
        Runnable action
) implements Comparable<ScheduledEvent> {

    ScheduledEvent {
        Objects.requireNonNull(time);
        Objects.requireNonNull(action);
    }

    void execute() {
        action.run();
    }

    @Override
    public int compareTo(ScheduledEvent other) {
        int byTime = time.compareTo(other.time);

        return byTime != 0 ?
                byTime :
                Long.compare(sequence, other.sequence);
    }
}
