package dev.l3m4rk.unreliable.simulation;

import org.junit.jupiter.api.Test;

import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScheduledEventTests {

    @Test
    void ordersEventsBySimulationTime() {
        var queue = new PriorityQueue<ScheduledEvent>();

        queue.add(new ScheduledEvent(new SimTime(200), 0, () -> {}));
        queue.add(new ScheduledEvent(new SimTime(100), 1, () -> {}));

        assertEquals(new SimTime(100), queue.remove().time());
        assertEquals(new SimTime(200), queue.remove().time());
    }

    @Test
    void preserverSchedulingOrderForEventsAtTheSameTime() {
        var queue = new PriorityQueue<ScheduledEvent>();

        var second = new ScheduledEvent(new SimTime(100), 2, () -> {});
        var first = new ScheduledEvent(new SimTime(100), 1, () -> {});

        queue.add(second);
        queue.add(first);

        assertEquals(first, queue.remove());
        assertEquals(second, queue.remove());
    }

    @Test
    void executesAction() {
        var counter = new AtomicInteger();

        var event = new ScheduledEvent(SimTime.ZERO, 0, counter::incrementAndGet);

        event.execute();

        assertEquals(1, counter.get());
    }
}
