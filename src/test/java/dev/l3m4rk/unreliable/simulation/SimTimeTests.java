package dev.l3m4rk.unreliable.simulation;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SimTimeTests {

    @Test
    void advancesSimulationTime() {
        var time = new SimTime(100);

        var result = time.plus(Duration.ofMillis(50));

        assertEquals(new SimTime(150), result);
    }

    @Test
    void rejectsNegativeTime() {
        assertThrows(IllegalArgumentException.class, () -> new SimTime(-1));
    }

    @Test
    void orderTimeChronologically() {
        var earlier = new SimTime(100);
        var later = new SimTime(200);

        assertEquals(-1, Integer.signum(earlier.compareTo(later)));
        assertEquals(1, Integer.signum(later.compareTo(earlier)));
        assertEquals(0, earlier.compareTo(new SimTime(100)));
    }
}