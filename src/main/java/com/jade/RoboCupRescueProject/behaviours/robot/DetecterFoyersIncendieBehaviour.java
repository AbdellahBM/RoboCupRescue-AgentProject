package com.jade.RoboCupRescueProject.behaviours.robot;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.*;

public class DetecterFoyersIncendieBehaviour extends CyclicBehaviour {
    private Map<String, FireHotspot> detectedHotspots;
    private static final double TEMPERATURE_THRESHOLD = 60.0; // Celsius
    private static final double INFRARED_THRESHOLD = 100.0;

    public DetecterFoyersIncendieBehaviour(Agent a) {
        super(a);
        this.detectedHotspots = new HashMap<>();
        System.out.println(myAgent.getLocalName() + ": Starting fire detection behavior");
    }

    @Override
    public void action() {
        // Perform thermal scan
        Map<String, Double> thermalData = performThermalScan();

        // Analyze thermal data
        analyzeHotspots(thermalData);

        // Report findings
        reportHotspots();

        block(500); // Scan every 500ms
    }

    private Map<String, Double> performThermalScan() {
        Map<String, Double> thermalData = new HashMap<>();

        // Simulate thermal camera readings
        double baseTemp = 25.0; // Base ambient temperature

        // Simulate multiple points in the scan area
        for (int i = 0; i < 5; i++) {
            String location = "POINT_" + i;
            double temperature = baseTemp + Math.random() * 100; // 25-125°C
            thermalData.put(location, temperature);
        }

        return thermalData;
    }

    private void analyzeHotspots(Map<String, Double> thermalData) {
        for (Map.Entry<String, Double> entry : thermalData.entrySet()) {
            String location = entry.getKey();
            double temperature = entry.getValue();

            if (temperature > TEMPERATURE_THRESHOLD) {
                // Confirm with infrared reading
                double infraredReading = getInfraredReading(location);

                if (infraredReading > INFRARED_THRESHOLD) {
                    // Confirmed hotspot
                    updateHotspot(location, temperature, infraredReading);
                }
            }
        }
    }

    private double getInfraredReading(String location) {
        // Simulate infrared sensor reading
        return Math.random() * 200; // 0-200 arbitrary units
    }

    private void updateHotspot(String location, double temperature, double infraredReading) {
        FireHotspot hotspot = new FireHotspot(location, temperature, infraredReading);

        if (!detectedHotspots.containsKey(location)) {
            // New hotspot detected
            detectedHotspots.put(location, hotspot);
            reportNewHotspot(hotspot);
        } else {
            // Update existing hotspot
            FireHotspot existing = detectedHotspots.get(location);
            if (Math.abs(existing.temperature - temperature) > 10) {
                detectedHotspots.put(location, hotspot);
                reportHotspotChange(hotspot);
            }
        }
    }

    private void reportNewHotspot(FireHotspot hotspot) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent("NEW_FIRE_HOTSPOT:" + hotspot.toString());
        myAgent.send(msg);
    }

    private void reportHotspotChange(FireHotspot hotspot) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent("HOTSPOT_UPDATE:" + hotspot.toString());
        myAgent.send(msg);
    }

    private void reportHotspots() {
        // Periodically report all active hotspots
        if (!detectedHotspots.isEmpty()) {
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            StringBuilder report = new StringBuilder("ACTIVE_HOTSPOTS:");

            for (FireHotspot hotspot : detectedHotspots.values()) {
                report.append("\n").append(hotspot.toString());
            }

            msg.setContent(report.toString());
            myAgent.send(msg);
        }
    }

    private static class FireHotspot {
        String location;
        double temperature;
        double infraredReading;
        long detectionTime;

        FireHotspot(String location, double temperature, double infraredReading) {
            this.location = location;
            this.temperature = temperature;
            this.infraredReading = infraredReading;
            this.detectionTime = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            return String.format("Location=%s,Temp=%.1f°C,IR=%.1f",
                    location, temperature, infraredReading);
        }
    }
}