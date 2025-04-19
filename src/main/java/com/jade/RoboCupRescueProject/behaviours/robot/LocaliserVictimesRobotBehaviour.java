package com.jade.RoboCupRescueProject.behaviours.robot;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class LocaliserVictimesRobotBehaviour extends CyclicBehaviour {
    // Thread-safe collections
    private final ConcurrentHashMap<String, VictimInfo> detectedVictims;
    private final CopyOnWriteArrayList<String> notifiedVictims;
    private final ConcurrentHashMap<String, Long> lastUpdateTimes;

    // Constants for detection thresholds
    private static final double HEARTBEAT_THRESHOLD = 0.5;
    private static final double MOVEMENT_THRESHOLD = 0.3;
    private static final double THERMAL_MIN = 36.0;
    private static final double THERMAL_MAX = 38.0;
    private static final long VICTIM_TIMEOUT = 10000; // 10 seconds
    private static final long SCAN_INTERVAL = 1000; // 1 second

    // Counters for statistics
    private final AtomicInteger totalVictimsFound;
    private final AtomicInteger activeVictims;

    // Cache for rescue team AIDs
    private List<AID> rescueTeams;
    private long lastTeamUpdate;
    private static final long TEAM_UPDATE_INTERVAL = 30000; // 30 seconds

    public LocaliserVictimesRobotBehaviour(Agent a) {
        super(a);
        this.detectedVictims = new ConcurrentHashMap<>();
        this.notifiedVictims = new CopyOnWriteArrayList<>();
        this.lastUpdateTimes = new ConcurrentHashMap<>();
        this.totalVictimsFound = new AtomicInteger(0);
        this.activeVictims = new AtomicInteger(0);
        this.rescueTeams = new ArrayList<>();

        System.out.println(myAgent.getLocalName() + ": Starting victim localization behavior");
        updateRescueTeams();
    }

    @Override
    public void action() {
        try {
            // Update rescue teams list periodically
            if (System.currentTimeMillis() - lastTeamUpdate > TEAM_UPDATE_INTERVAL) {
                updateRescueTeams();
            }

            // Scan for victims
            Map<String, DetectionData> currentDetections = performAreaScan();

            // Process detections and update victim status
            processDetections(currentDetections);

            // Update existing victim statuses
            updateVictimStatuses();

            // Guide rescue teams
            if (!detectedVictims.isEmpty()) {
                guideRescueTeams();
            }

            // Wait before next scan
            block(SCAN_INTERVAL);
        } catch (Exception e) {
            System.err.println(myAgent.getLocalName() + ": Error in victim localization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Map<String, DetectionData> performAreaScan() {
        Map<String, DetectionData> detections = new HashMap<>();

        // Simulate scanning different zones
        for (int i = 0; i < 3; i++) {
            String zone = "ZONE_" + i;
            DetectionData data = new DetectionData(
                    detectHeartbeat(zone),
                    detectMovement(zone),
                    detectThermalSignature(zone)
            );

            if (data.isSignificant()) {
                detections.put(zone, data);
            }
        }

        return detections;
    }

    private double detectHeartbeat(String zone) {
        // Simulate heartbeat detection with random noise
        return Math.random() + (Math.random() * 0.2);
    }

    private double detectMovement(String zone) {
        // Simulate movement detection with random noise
        return Math.random() + (Math.random() * 0.15);
    }

    private double detectThermalSignature(String zone) {
        // Simulate thermal detection around human body temperature
        return 36.5 + (Math.random() * 2.0);
    }

    private void processDetections(Map<String, DetectionData> detections) {
        for (Map.Entry<String, DetectionData> entry : detections.entrySet()) {
            String zone = entry.getKey();
            DetectionData data = entry.getValue();

            double confidence = calculateConfidence(data);

            if (confidence > 0.7) { // 70% confidence threshold
                updateVictimLocation(zone, confidence, data);
            }
        }
    }

    private double calculateConfidence(DetectionData data) {
        double confidence = 0.0;

        // Weight different factors
        if (data.heartbeat > HEARTBEAT_THRESHOLD) confidence += 0.4;
        if (data.movement > MOVEMENT_THRESHOLD) confidence += 0.3;
        if (data.thermal > THERMAL_MIN && data.thermal < THERMAL_MAX) confidence += 0.3;

        return confidence;
    }

    private void updateVictimLocation(String zone, double confidence, DetectionData data) {
        VictimInfo victim = new VictimInfo(zone, confidence, data);

        VictimInfo previous = detectedVictims.put(zone, victim);
        lastUpdateTimes.put(zone, System.currentTimeMillis());

        if (previous == null) {
            // New victim detected
            totalVictimsFound.incrementAndGet();
            activeVictims.incrementAndGet();
            notifyNewVictim(victim);
        }
    }

    private void notifyNewVictim(VictimInfo victim) {
        if (!notifiedVictims.contains(victim.location)) {
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setContent(formatVictimMessage(victim));

            // Add all rescue teams as receivers
            for (AID team : rescueTeams) {
                msg.addReceiver(team);
            }

            myAgent.send(msg);
            notifiedVictims.add(victim.location);

            System.out.println(myAgent.getLocalName() + ": Notified rescue teams about victim at " + victim.location);
        }
    }

    private void updateVictimStatuses() {
        long currentTime = System.currentTimeMillis();

        // Create a copy of locations to avoid concurrent modification
        Set<String> locations = new HashSet<>(detectedVictims.keySet());

        for (String location : locations) {
            Long lastUpdate = lastUpdateTimes.get(location);
            if (lastUpdate != null && (currentTime - lastUpdate > VICTIM_TIMEOUT)) {
                // Remove victim if not detected recently
                detectedVictims.remove(location);
                lastUpdateTimes.remove(location);
                notifiedVictims.remove(location);
                activeVictims.decrementAndGet();

                // Notify about victim status change
                notifyVictimStatusChange(location, "TIMEOUT");
            }
        }
    }

    private void guideRescueTeams() {
        // Create a snapshot of current victims
        Map<String, VictimInfo> victims = new HashMap<>(detectedVictims);

        for (Map.Entry<String, VictimInfo> entry : victims.entrySet()) {
            VictimInfo victim = entry.getValue();

            if (victim.confidence > 0.5) {
                sendGuidanceMessage(victim);
            }
        }
    }

    private void sendGuidanceMessage(VictimInfo victim) {
        ACLMessage guidance = new ACLMessage(ACLMessage.INFORM);
        guidance.setContent("RESCUE_GUIDANCE:" + formatVictimMessage(victim));

        for (AID team : rescueTeams) {
            guidance.addReceiver(team);
        }

        myAgent.send(guidance);
    }

    private void updateRescueTeams() {
        try {
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("rescue-team");
            template.addServices(sd);

            DFAgentDescription[] result = DFService.search(myAgent, template);

            rescueTeams = new ArrayList<>();
            for (DFAgentDescription agent : result) {
                rescueTeams.add(agent.getName());
            }

            lastTeamUpdate = System.currentTimeMillis();
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private String formatVictimMessage(VictimInfo victim) {
        return String.format("VICTIM:%s:confidence=%.2f:heartbeat=%.2f:movement=%.2f:thermal=%.1f",
                victim.location,
                victim.confidence,
                victim.data.heartbeat,
                victim.data.movement,
                victim.data.thermal
        );
    }

    private void notifyVictimStatusChange(String location, String status) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent("VICTIM_STATUS:" + location + ":" + status);

        for (AID team : rescueTeams) {
            msg.addReceiver(team);
        }

        myAgent.send(msg);
    }

    // Inner classes for data representation
    private static class DetectionData {
        final double heartbeat;
        final double movement;
        final double thermal;

        DetectionData(double heartbeat, double movement, double thermal) {
            this.heartbeat = heartbeat;
            this.movement = movement;
            this.thermal = thermal;
        }

        boolean isSignificant() {
            return heartbeat > HEARTBEAT_THRESHOLD ||
                    movement > MOVEMENT_THRESHOLD ||
                    (thermal > THERMAL_MIN && thermal < THERMAL_MAX);
        }
    }

    private static class VictimInfo {
        final String location;
        final double confidence;
        final DetectionData data;
        final long timestamp;

        VictimInfo(String location, double confidence, DetectionData data) {
            this.location = location;
            this.confidence = confidence;
            this.data = data;
            this.timestamp = System.currentTimeMillis();
        }
    }
}