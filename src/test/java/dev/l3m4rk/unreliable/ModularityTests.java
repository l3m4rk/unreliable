package dev.l3m4rk.unreliable;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

public class ModularityTests {
    @Test
    void verifiesModularStructure() {
        ApplicationModules
                .of(UnreliableApplication.class)
                .verify();
    }
}
