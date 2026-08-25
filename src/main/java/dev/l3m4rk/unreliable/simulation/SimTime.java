package dev.l3m4rk.unreliable.simulation;

import java.time.Duration;

public record SimTime(long millis) implements Comparable<SimTime> {

    public static final SimTime ZERO = new SimTime(0);

    public SimTime {
        if (millis < 0) {
            throw new IllegalArgumentException("Duration cannot be negative");
        }
    }

    public SimTime plus(Duration duration) {
        if (duration.isNegative()) {
            throw new IllegalArgumentException("Duration cannot be negative");
        }

        return  new SimTime(Math.addExact(millis, duration.toMillis()));
    }

    @Override
    public int compareTo(SimTime other) {
        return Long.compare(millis, other.millis);
    }
}
