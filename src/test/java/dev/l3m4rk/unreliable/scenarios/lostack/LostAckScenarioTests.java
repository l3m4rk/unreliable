package dev.l3m4rk.unreliable.scenarios.lostack;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LostAckScenarioTests {
    @Test
    void duplicateChargeOccursWhenResponseIsLost() {
        var scenario = new LostAckScenario();

        scenario.start();
        scenario.runToCompletion();

        assertEquals(2, scenario.attempts());

        assertEquals(2, scenario.chargeCount());
        assertEquals(2_000, scenario.totalChargedCents());

        assertTrue(scenario.response().isPresent());
    }

    @Test
    void canAdvanceScenarioOneEventAtATime() {
        var scenario = new LostAckScenario();

        scenario.start();

        assertEquals(0, scenario.now().millis());

        assertTrue(scenario.step());
    }
}