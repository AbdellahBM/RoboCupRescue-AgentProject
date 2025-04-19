package com.jade.RoboCupRescueProject.behaviours.robot;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.*;

public class ExplorerZoneBehaviour extends TickerBehaviour {
    private Position currentPosition;
    private List<Position> exploredPositions;
    private Map<String, Double> terrainDifficulty;
    private static final double SCAN_RADIUS = 5.0;

    public ExplorerZoneBehaviour(Agent a, long period) {
        super(a, period);
        this.currentPosition = new Position(0, 0);
        this.exploredPositions = new ArrayList<>();
        this.terrainDifficulty = new HashMap<>();
        System.out.println(myAgent.getLocalName() + ": Starting exploration behavior");
    }

    @Override
    protected void onTick() {
        // Explore new area
        exploreArea();

        // Send sensor data
        sendEnvironmentData();

        // Update position
        updatePosition();
    }

    private void exploreArea() {
        // Simulate area scanning
        Map<String, SensorData> areaData = scanCurrentArea();

        // Process and analyze terrain
        analyzeTerrain(areaData);

        // Mark position as explored
        exploredPositions.add(new Position(currentPosition));
    }

    private Map<String, SensorData> scanCurrentArea() {
        Map<String, SensorData> sensorData = new HashMap<>();

        // Simulate different sensor readings
        double temperature = 20 + Math.random() * 30; // 20-50°C
        double radiation = Math.random() * 10; // 0-10 mSv
        double airQuality = Math.random() * 100; // 0-100%

        sensorData.put("TEMPERATURE", new SensorData("temperature", temperature));
        sensorData.put("RADIATION", new SensorData("radiation", radiation));
        sensorData.put("AIR_QUALITY", new SensorData("airQuality", airQuality));

        return sensorData;
    }

    private void analyzeTerrain(Map<String, SensorData> sensorData) {
        // Calculate terrain difficulty based on sensor data
        double difficulty = calculateTerrainDifficulty(sensorData);

        String locationKey = currentPosition.toString();
        terrainDifficulty.put(locationKey, difficulty);

        // Report if terrain is too dangerous
        if (difficulty > 0.8) { // 80% difficulty threshold
            reportDangerousArea();
        }
    }

    private double calculateTerrainDifficulty(Map<String, SensorData> sensorData) {
        double difficulty = 0.0;

        // Factor in temperature
        double temp = sensorData.get("TEMPERATURE").value;
        difficulty += (temp > 40) ? 0.4 : (temp > 30 ? 0.2 : 0.0);

        // Factor in radiation
        double rad = sensorData.get("RADIATION").value;
        difficulty += (rad > 8) ? 0.4 : (rad > 5 ? 0.2 : 0.0);

        // Factor in air quality
        double air = sensorData.get("AIR_QUALITY").value;
        difficulty += (air < 20) ? 0.4 : (air < 50 ? 0.2 : 0.0);

        return Math.min(1.0, difficulty);
    }

    private void reportDangerousArea() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent("DANGEROUS_AREA:" + currentPosition +
                ",difficulty=" + terrainDifficulty.get(currentPosition.toString()));
        myAgent.send(msg);
    }

    private void sendEnvironmentData() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        StringBuilder data = new StringBuilder("ENVIRONMENT_DATA:");
        data.append(currentPosition).append(";");
        data.append(scanCurrentArea().toString());
        msg.setContent(data.toString());
        myAgent.send(msg);
    }

    private void updatePosition() {
        // Calculate next position based on exploration strategy
        Position nextPosition = calculateNextPosition();
        currentPosition = nextPosition;
    }

    private Position calculateNextPosition() {
        // Simple exploration pattern
        double angle = Math.random() * 2 * Math.PI;
        double distance = Math.random() * SCAN_RADIUS;

        double newX = currentPosition.x + distance * Math.cos(angle);
        double newY = currentPosition.y + distance * Math.sin(angle);

        return new Position(newX, newY);
    }

    private static class Position {
        double x, y;

        Position(double x, double y) {
            this.x = x;
            this.y = y;
        }

        Position(Position other) {
            this.x = other.x;
            this.y = other.y;
        }

        @Override
        public String toString() {
            return String.format("(%.2f,%.2f)", x, y);
        }
    }

    private static class SensorData {
        String type;
        double value;

        SensorData(String type, double value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public String toString() {
            return String.format("%s:%.2f", type, value);
        }
    }
}