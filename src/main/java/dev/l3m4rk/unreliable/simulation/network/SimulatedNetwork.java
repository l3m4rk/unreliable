package dev.l3m4rk.unreliable.simulation.network;

import dev.l3m4rk.unreliable.simulation.NodeId;
import dev.l3m4rk.unreliable.simulation.SimTime;
import dev.l3m4rk.unreliable.simulation.SimulationContext;
import dev.l3m4rk.unreliable.simulation.SimulationEngine;

import java.time.Duration;
import java.util.*;

public final class SimulatedNetwork {
    private final SimulationEngine engine;
    private final Map<NodeId, SimNode> nodes = new HashMap<>();
    private final List<NetworkRule> rules = new ArrayList<>();

    private Duration latency = Duration.ZERO;

    public SimulatedNetwork(SimulationEngine engine) {
        this.engine = Objects.requireNonNull(engine);
    }

    public void register(SimNode node) {
        Objects.requireNonNull(node);

        var previous = nodes.putIfAbsent(node.id(), node);

        if (previous != null) {
            throw new IllegalArgumentException("Node is already registered: " + node.id());
        }
    }

    public void addRule(NetworkRule rule) {
        rules.add(Objects.requireNonNull(rule));
    }

    public void latency(Duration latency) {
        Objects.requireNonNull(latency);

        if (latency.isNegative()) {
            throw new IllegalArgumentException("Network latency cannot be negative");
        }

        this.latency = latency;
    }

    public void send(NodeId source, NodeId destination, Message message) {
        var envelope = new Envelope(source, destination, message);

        engine.schedule(latency, () -> deliver(envelope));
    }

    public SimulationContext contextFor(NodeId nodeId) {
        if (!nodes.containsKey(nodeId)) {
            throw new IllegalArgumentException("Unknown node: " + nodeId);
        }

        return new NodeSimulationContext(nodeId);
    }

    private void deliver(Envelope envelope) {
        if (shouldDrop(envelope)) {
            return;
        }

        var node = nodes.get(envelope.destination());

        if (node == null) {
            throw new IllegalStateException("Unknown destination node: " + envelope.destination());
        }

        node.receive(envelope, new NodeSimulationContext(envelope.destination()));
    }

    private boolean shouldDrop(Envelope envelope) {
        return rules.stream().anyMatch(rule -> rule.shouldDrop(envelope));
    }

    private final class NodeSimulationContext implements SimulationContext {
        private final NodeId source;

        private NodeSimulationContext(NodeId source) {
            this.source = source;
        }

        @Override
        public SimTime now() {
            return engine.now();
        }

        @Override
        public void send(NodeId destination, Message message) {
            SimulatedNetwork.this.send(source, destination, message);
        }

        @Override
        public void schedule(Duration delay, Runnable action) {
            engine.schedule(delay, action);
        }
    }
}
