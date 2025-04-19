package com.jade.RoboCupRescueProject.behaviours.pompier;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.*;

public class DetecterIncendieBehaviour extends TickerBehaviour {
    private Map<String, FireInfo> detectedFires;
    private final double DETECTION_RADIUS = 100.0; // meters
    private double currentX = 0.0;
    private double currentY = 0.0;

    public DetecterIncendieBehaviour(Agent a, long period) {
        super(a, period);
        detectedFires = new HashMap<>();
        System.out.println(a.getLocalName() + ": Starting fire detection behavior");
    }

    @Override
    protected void onTick() {
        // Simulate sensor readings
        scanForFires();

        // Update patrol position
        updatePosition();
    }

    private void scanForFires() {
        // Simulate fire detection using sensors
        Map<String, Double> sensorReadings = getSensorReadings();

        for (Map.Entry<String, Double> reading : sensorReadings.entrySet()) {
            String location = reading.getKey();
            double heatLevel = reading.getValue();

            if (heatLevel > 50.0) { // Temperature threshold for fire detection
                FireInfo fireInfo = new FireInfo(location, heatLevel);

                if (!detectedFires.containsKey(location)) {
                    // New fire detected
                    detectedFires.put(location, fireInfo);
                    notifyFireDetection(fireInfo);
                } else if (Math.abs(detectedFires.get(location).heatLevel - heatLevel) > 10.0) {
                    // Significant change in existing fire
                    detectedFires.put(location, fireInfo);
                    notifyFireChange(fireInfo);
                }
            }
        }
    }

    private Map<String, Double> getSensorReadings() {
        Map<String, Double> readings = new HashMap<>();
        // Simulate readings from different sensors
        readings.put(String.format("ZONE_%d_%d", (int)currentX, (int)currentY),
                Math.random() * 100);
        return readings;
    }

    private void notifyFireDetection(FireInfo fireInfo) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent("FIRE_DETECTED:" + fireInfo.location + "," + fireInfo.heatLevel);
        myAgent.send(msg);

        // Also notify team leader
        ACLMessage leaderMsg = new ACLMessage(ACLMessage.INFORM);
        leaderMsg.setContent("FIRE_REPORT:" + fireInfo.location + "," + fireInfo.heatLevel);
        myAgent.send(leaderMsg);
    }

    private void notifyFireChange(FireInfo fireInfo) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent("FIRE_UPDATE:" + fireInfo.location + "," + fireInfo.heatLevel);
        myAgent.send(msg);
    }

    private void updatePosition() {
        // Simulate movement pattern for patrolling
        currentX += 10 * Math.cos(System.currentTimeMillis() / 1000.0);
        currentY += 10 * Math.sin(System.currentTimeMillis() / 1000.0);
    }

    private static class FireInfo {
        final String location;
        final double heatLevel;
        final long detectionTime;

        FireInfo(String location, double heatLevel) {
            this.location = location;
            this.heatLevel = heatLevel;
            this.detectionTime = System.currentTimeMillis();
        }
    }
}