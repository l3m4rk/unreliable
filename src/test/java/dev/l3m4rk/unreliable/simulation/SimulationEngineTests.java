package dev.l3m4rk.unreliable.simulation;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimulationEngineTests {

    @Test
    void startsAtZero() {
        var engine = new SimulationEngine();

        assertEquals(SimTime.ZERO, engine.now());
    }

    @Test
    void executesEarliestScheduledEventsFirst() {
        var engine = new SimulationEngine();
        var executed = new ArrayList<String>();

        engine.schedule(Duration.ofMillis(100), () -> executed.add("later"));
        engine.schedule(Duration.ofMillis(50), () -> executed.add("earlier"));

        assertTrue(engine.step());

        assertEquals(List.of("earlier"), executed);
        assertEquals(new SimTime(50), engine.now());
    }

    @Test
    void preservesSchedulingOrderAtTheSameTime() {
        var engine = new SimulationEngine();
        var executed = new ArrayList<String>();

        engine.schedule(Duration.ofMillis(100), () -> executed.add("first"));
        engine.schedule(Duration.ofMillis(100), () -> executed.add("second"));

        engine.step();
        engine.step();

        assertEquals(List.of("first", "second"), executed);
    }

    @Test
    void schedulesRelativeToTheCurrentSimulationTime() {
        var engine = new SimulationEngine();

        engine.schedule(Duration.ofMillis(100), () -> {});

        engine.step();

        engine.schedule(Duration.ofMillis(50), () -> {});

        engine.step();

        assertEquals(new SimTime(150), engine.now());
    }

    @Test
    void returnsFalseWhenThereAreNoEvents() {
        var engine = new SimulationEngine();

        assertFalse(engine.step());
    }
}