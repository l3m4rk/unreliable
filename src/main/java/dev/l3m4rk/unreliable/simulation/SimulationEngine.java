package dev.l3m4rk.unreliable.simulation;

import java.time.Duration;
import java.util.Objects;
import java.util.PriorityQueue;

public final class SimulationEngine {
    private final PriorityQueue<ScheduledEvent> events = new PriorityQueue<>();

    private SimTime now = SimTime.ZERO;
    private long nextSequence = 0;

    public SimTime now() {
        return now;
    }

    public void schedule(Duration delay, Runnable action) {
        Objects.requireNonNull(delay);
        Objects.requireNonNull(action);

        var executionTime = now.plus(delay);

        events.add(new ScheduledEvent(executionTime, nextSequence++, action));
    }

    public boolean step() {
        var event = events.poll();

        if (event == null) {
            return false;
        }

        now = event.time();
        event.execute();

        return true;
    }
}
