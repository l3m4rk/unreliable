package dev.l3m4rk.unreliable.simulation.network;

import dev.l3m4rk.unreliable.simulation.NodeId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DropNextTests {

    @Test
    void dropFirstMatchingMessageOnly() {
        var rule = new DropNext(TestMessage.class);

        var envelope = new Envelope(new NodeId("a"), new NodeId("b"), new TestMessage());

        assertThat(rule.shouldDrop(envelope)).isTrue();
        assertThat(rule.shouldDrop(envelope)).isFalse();
    }

    @Test
    void ignoresDifferentMessageTypes() {
        var rule = new DropNext(TestMessage.class);

        var other = new Envelope(new NodeId("a"), new NodeId("b"), new OtherMessage());

        assertThat(rule.shouldDrop(other)).isFalse();
    }

    private record TestMessage() implements Message {
    }
    private record OtherMessage() implements Message {
    }
}