package com.jade.RoboCupRescueProject.behaviours.ambulancier;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class LocaliserVictimesBehaviour extends CyclicBehaviour {
    private final ConcurrentHashMap<String, VictimInfo> detectedVictims;
    private final AtomicBoolean isRunning;
    private Position currentPosition;
    private final int gridSize = 60;  // Size of the search grid
    private final Random random;
    private static final long SCAN_INTERVAL = 1000; // 1 second between movements

    public LocaliserVictimesBehaviour(Agent a) {
        super(a);
        this.detectedVictims = new ConcurrentHashMap<>();
        this.isRunning = new AtomicBoolean(true);
        this.currentPosition = new Position(0, 0);
        this.random = new Random();
        System.out.println(myAgent.getLocalName() + ": Starting victim search behavior");
    }

    @Override
    public void action() {
        try {
            if (!isRunning.get()) {
                return;
            }

            // Move to next position
            moveToNextPosition();

            // Scan for victims
            scanForVictims();

            // Controlled delay with interrupt handling
            try {
                Thread.sleep(SCAN_INTERVAL);
            } catch (InterruptedException e) {
                System.out.println(myAgent.getLocalName() + ": Search behavior interrupted, cleaning up...");
                isRunning.set(false);
                Thread.currentThread().interrupt();
                return;
            }

        } catch (Exception e) {
            System.err.println(myAgent.getLocalName() + ": Error in victim search: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void moveToNextPosition() {
        // Implement systematic search pattern
        int nextX = currentPosition.x;
        int nextY = currentPosition.y;

        // Simple pattern: move right and up in a grid
        if (nextX < gridSize) {
            nextX++;
        } else if (nextY < gridSize) {
            nextX = 0;
            nextY++;
        } else {
            // Reset to start when reaching the end
            nextX = 0;
            nextY = 0;
        }

        currentPosition = new Position(nextX, nextY);
        // Removed patrol position logging to reduce console clutter
    }

    private void scanForVictims() {
        // Simulate victim detection with some randomization
        if (random.nextDouble() < 0.02) { // 2% chance to find a victim
            // Generate victim position near current position
            int victimX = currentPosition.x + random.nextInt(11) - 5;
            int victimY = currentPosition.y + random.nextInt(11) - 5;

            // Ensure coordinates are within grid
            victimX = Math.max(0, Math.min(victimX, gridSize));
            victimY = Math.max(0, Math.min(victimY, gridSize));

            Position victimPosition = new Position(victimX, victimY);

            // Check if victim already detected
            if (!detectedVictims.containsKey(victimPosition.toString())) {
                handleNewVictim(victimPosition);
            }
        }
    }

    private void handleNewVictim(Position position) {
        // Create victim info
        VictimInfo victim = new VictimInfo(position, System.currentTimeMillis());
        detectedVictims.put(position.toString(), victim);

        // Inform other agents
        System.out.println(myAgent.getLocalName() + ": Found a victim at " + position);

        // Send information to other agents
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent("VICTIM_FOUND:" + position.x + "," + position.y);
        myAgent.send(msg);

        System.out.println(myAgent.getLocalName() + ": Informed other agents about victim at " + position);
    }

    @Override
    public void onStart() {
        isRunning.set(true);
    }

    @Override
    public int onEnd() {
        isRunning.set(false);
        return super.onEnd();
    }

    // Helper classes
    private static class Position {
        final int x;
        final int y;

        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "(" + x + "," + y + ")";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Position position = (Position) o;
            return x == position.x && y == position.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    private static class VictimInfo {
        final Position position;
        final long detectionTime;

        VictimInfo(Position position, long detectionTime) {
            this.position = position;
            this.detectionTime = detectionTime;
        }
    }
}
