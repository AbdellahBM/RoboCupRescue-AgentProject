package com.jade.RoboCupRescueProject.behaviours.pompier;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.*;

public class SignalerEtatFeuBehaviour extends TickerBehaviour {
    private Map<String, FireStatus> activeFireStatus;
    private final EteindreIncendieBehaviour extinguishBehaviour;

    public SignalerEtatFeuBehaviour(Agent a, long period, EteindreIncendieBehaviour extinguishBehaviour) {
        super(a, period);
        this.extinguishBehaviour = extinguishBehaviour;
        this.activeFireStatus = new HashMap<>();
        System.out.println(a.getLocalName() + ": Starting fire status reporting behavior");
    }

    @Override
    protected void onTick() {
        updateFireStatus();
        sendStatusReport();
    }

    private void updateFireStatus() {
        // Update status of known fires
        for (FireStatus status : activeFireStatus.values()) {
            status.updateStatus(extinguishBehaviour.getWaterLevel());
        }
    }

    private void sendStatusReport() {
        StringBuilder report = new StringBuilder("FIRE_STATUS_REPORT:\n");

        for (Map.Entry<String, FireStatus> entry : activeFireStatus.entrySet()) {
            FireStatus status = entry.getValue();
            report.append(String.format("Location: %s\n", entry.getKey()));
            report.append(String.format("Status: %s\n", status.getStatusDescription()));
            report.append(String.format("Intensity: %s\n", status.getIntensityLevel()));
            report.append(String.format("Control Level: %d%%\n", status.getControlLevel()));
        }

        // Add resource status
        report.append(String.format("Water Level: %.2f L\n", extinguishBehaviour.getWaterLevel()));

        // Send report to team leader
        ACLMessage reportMsg = new ACLMessage(ACLMessage.INFORM);
        reportMsg.setContent(report.toString());
        myAgent.send(reportMsg);
    }

    public void addFireLocation(String location, double intensity) {
        activeFireStatus.put(location, new FireStatus(intensity));
    }

    public void removeFireLocation(String location) {
        activeFireStatus.remove(location);
    }

    private static class FireStatus {
        private double intensity;
        private int controlLevel;
        private long lastUpdate;

        FireStatus(double intensity) {
            this.intensity = intensity;
            this.controlLevel = 0;
            this.lastUpdate = System.currentTimeMillis();
        }

        void updateStatus(double waterLevel) {
            // Update control level based on water availability and time
            if (waterLevel > 0) {
                controlLevel = Math.min(100, controlLevel + 5);
                intensity = Math.max(0, intensity - 0.1);
            }
            lastUpdate = System.currentTimeMillis();
        }

        String getStatusDescription() {
            if (controlLevel >= 90) return "UNDER_CONTROL";
            if (controlLevel >= 50) return "PARTIALLY_CONTROLLED";
            if (controlLevel >= 20) return "BEING_FOUGHT";
            return "ACTIVE";
        }

        String getIntensityLevel() {
            if (intensity >= 80) return "EXTREME";
            if (intensity >= 50) return "HIGH";
            if (intensity >= 20) return "MODERATE";
            return "LOW";
        }

        int getControlLevel() {
            return controlLevel;
        }
    }
}