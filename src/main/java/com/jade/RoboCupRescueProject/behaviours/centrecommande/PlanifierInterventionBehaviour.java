package com.jade.RoboCupRescueProject.behaviours.centrecommande;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import com.jade.RoboCupRescueProject.agents.AgentCentreCommande;

/**
 * Behavior responsible for defining priorities for operations.
 * This behavior analyzes the collected data, determines the order of priority
 * (extinguishing a larger fire, rescuing a large group of injured people, etc.),
 * and updates the command center's mission priorities.
 */
public class PlanifierInterventionBehaviour extends CyclicBehaviour {
    // Mission priority levels
    private static final int PRIORITY_CRITICAL = 1;
    private static final int PRIORITY_HIGH = 2;
    private static final int PRIORITY_MEDIUM = 3;
    private static final int PRIORITY_LOW = 4;

    // Mission types
    private static final String MISSION_FIRE_FIGHTING = "FIRE_FIGHTING";
    private static final String MISSION_VICTIM_RESCUE = "VICTIM_RESCUE";
    private static final String MISSION_ROAD_CLEARING = "ROAD_CLEARING";
    private static final String MISSION_SECURITY_ZONE = "SECURITY_ZONE";

    private Random random = new Random();
    private long lastPlanningTime = 0;
    private static final long PLANNING_INTERVAL = 5000; // 5 seconds

    public PlanifierInterventionBehaviour(Agent a) { 
        super(a); 
        // Removed startup logging to reduce console clutter
    }

    @Override
    public void action() {
        // Only plan at regular intervals
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPlanningTime < PLANNING_INTERVAL) {
            block(PLANNING_INTERVAL - (currentTime - lastPlanningTime));
            return;
        }

        lastPlanningTime = currentTime;

        // Update agent status
        ((AgentCentreCommande)myAgent).updateStatus("PLANNING");

        // Removed planning intervention priorities logging to reduce console clutter

        // Get the current state of the disaster area
        AgentCentreCommande agent = (AgentCentreCommande)myAgent;

        // Create a list of potential missions
        List<Mission> missions = new ArrayList<>();

        // Add fire fighting missions
        addFireFightingMissions(missions, agent);

        // Add victim rescue missions
        addVictimRescueMissions(missions, agent);

        // Add road clearing missions
        addRoadClearingMissions(missions, agent);

        // Add security zone missions
        addSecurityZoneMissions(missions, agent);

        // Sort missions by priority
        missions.sort(Comparator.comparingInt(Mission::getPriority));

        // Update the agent's priority missions
        updatePriorityMissions(missions, agent);

        // Removed planning completed logging to reduce console clutter

        // Wait before next planning cycle
        block(PLANNING_INTERVAL);
    }

    /**
     * Add fire fighting missions to the list
     * @param missions The list of missions
     * @param agent The command center agent
     */
    private void addFireFightingMissions(List<Mission> missions, AgentCentreCommande agent) {
        // In a real implementation, this would analyze the fire reports
        // For simplicity, we'll just create some simulated missions

        // Check if there are any priority missions related to fires
        List<String> priorityMissions = agent.getPriorityMissions();
        for (String mission : priorityMissions) {
            if (mission.startsWith("EXTINGUISH_FIRE:")) {
                String location = mission.substring("EXTINGUISH_FIRE:".length());
                missions.add(new Mission(MISSION_FIRE_FIGHTING, location, PRIORITY_HIGH, 
                                        "Extinguish fire at " + location));
            }
        }

        // Add some random fire fighting missions
        if (random.nextDouble() < 0.3) { // 30% chance
            String location = "Building_" + (random.nextInt(10) + 1);
            int priority = random.nextInt(3) + 2; // PRIORITY_HIGH to PRIORITY_LOW
            missions.add(new Mission(MISSION_FIRE_FIGHTING, location, priority, 
                                    "Extinguish fire at " + location));
        }
    }

    /**
     * Add victim rescue missions to the list
     * @param missions The list of missions
     * @param agent The command center agent
     */
    private void addVictimRescueMissions(List<Mission> missions, AgentCentreCommande agent) {
        // Check if there are any priority missions related to victims
        List<String> priorityMissions = agent.getPriorityMissions();
        for (String mission : priorityMissions) {
            if (mission.startsWith("RESCUE_CRITICAL_VICTIMS:")) {
                String location = mission.substring("RESCUE_CRITICAL_VICTIMS:".length());
                missions.add(new Mission(MISSION_VICTIM_RESCUE, location, PRIORITY_CRITICAL, 
                                        "Rescue critical victims reported by " + location));
            }
        }

        // Add some random victim rescue missions
        if (random.nextDouble() < 0.4) { // 40% chance
            String location = "Area_" + (random.nextInt(10) + 1);
            int priority = random.nextInt(3) + 1; // PRIORITY_CRITICAL to PRIORITY_MEDIUM
            missions.add(new Mission(MISSION_VICTIM_RESCUE, location, priority, 
                                    "Search and rescue at " + location));
        }
    }

    /**
     * Add road clearing missions to the list
     * @param missions The list of missions
     * @param agent The command center agent
     */
    private void addRoadClearingMissions(List<Mission> missions, AgentCentreCommande agent) {
        // Check if there are any priority missions related to roads
        List<String> priorityMissions = agent.getPriorityMissions();
        for (String mission : priorityMissions) {
            if (mission.startsWith("CLEAR_ROADS:")) {
                String location = mission.substring("CLEAR_ROADS:".length());
                missions.add(new Mission(MISSION_ROAD_CLEARING, location, PRIORITY_MEDIUM, 
                                        "Clear roads reported by " + location));
            }
        }

        // Add some random road clearing missions
        if (random.nextDouble() < 0.2) { // 20% chance
            String roadId = "ROAD_" + (random.nextInt(5) + 1);
            int priority = random.nextInt(2) + 3; // PRIORITY_MEDIUM to PRIORITY_LOW
            missions.add(new Mission(MISSION_ROAD_CLEARING, roadId, priority, 
                                    "Clear debris from " + roadId));
        }
    }

    /**
     * Add security zone missions to the list
     * @param missions The list of missions
     * @param agent The command center agent
     */
    private void addSecurityZoneMissions(List<Mission> missions, AgentCentreCommande agent) {
        // Check if there are any priority missions related to security zones
        List<String> priorityMissions = agent.getPriorityMissions();
        for (String mission : priorityMissions) {
            if (mission.startsWith("MONITOR_SECURITY_ZONES:")) {
                String location = mission.substring("MONITOR_SECURITY_ZONES:".length());
                missions.add(new Mission(MISSION_SECURITY_ZONE, location, PRIORITY_MEDIUM, 
                                        "Monitor security zones reported by " + location));
            }
        }

        // Add some random security zone missions
        if (random.nextDouble() < 0.15) { // 15% chance
            String zoneId = "ZONE_" + (random.nextInt(3) + 1);
            int priority = random.nextInt(2) + 2; // PRIORITY_HIGH to PRIORITY_MEDIUM
            missions.add(new Mission(MISSION_SECURITY_ZONE, zoneId, priority, 
                                    "Establish security perimeter at " + zoneId));
        }
    }

    /**
     * Update the agent's priority missions
     * @param missions The list of missions
     * @param agent The command center agent
     */
    private void updatePriorityMissions(List<Mission> missions, AgentCentreCommande agent) {
        // Clear existing priority missions
        List<String> currentMissions = agent.getPriorityMissions();
        for (String mission : currentMissions) {
            // Keep only missions that were added by other behaviors
            if (!mission.startsWith("MISSION:")) {
                continue;
            }

            // Remove this mission
            agent.getPriorityMissions().remove(mission);
        }

        // Add new priority missions
        for (Mission mission : missions) {
            String missionStr = "MISSION:" + mission.getType() + ":" + mission.getLocation() + 
                               ":" + mission.getPriority() + ":" + mission.getDescription();
            agent.addPriorityMission(missionStr);

            // Removed added priority mission logging to reduce console clutter
        }
    }

    /**
     * Inner class representing a mission
     */
    private static class Mission {
        private String type;
        private String location;
        private int priority;
        private String description;

        public Mission(String type, String location, int priority, String description) {
            this.type = type;
            this.location = location;
            this.priority = priority;
            this.description = description;
        }

        public String getType() {
            return type;
        }

        public String getLocation() {
            return location;
        }

        public int getPriority() {
            return priority;
        }

        public String getDescription() {
            return description;
        }
    }
}
