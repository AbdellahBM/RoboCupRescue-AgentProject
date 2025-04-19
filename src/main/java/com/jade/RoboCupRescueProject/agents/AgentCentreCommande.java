package com.jade.RoboCupRescueProject.agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.core.behaviours.TickerBehaviour;
import com.jade.RoboCupRescueProject.behaviours.centrecommande.CollecterInfosBehaviour;
import com.jade.RoboCupRescueProject.behaviours.centrecommande.PlanifierInterventionBehaviour;
import com.jade.RoboCupRescueProject.behaviours.centrecommande.DispatcherMissionsBehaviour;
import com.jade.RoboCupRescueProject.behaviours.centrecommande.GererRessourcesGlobalesBehaviour;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * Agent Centre Commande (Command Center Agent)
 * 
 * Responsibilities:
 * 1. Coordinate all rescue operations (resource allocation, priority order, etc.)
 * 2. Centralize information from Firefighters, Ambulance, and Police
 * 3. Make strategic decisions (where to send reinforcements, when to trigger an evacuation, etc.)
 * 
 * This agent uses four behaviors to fulfill its responsibilities:
 * - CollecterInfosBehaviour: Collect and aggregate data from different agents
 * - PlanifierInterventionBehaviour: Define priorities for operations
 * - DispatcherMissionsBehaviour: Assign tasks to different agents
 * - GererRessourcesGlobalesBehaviour: Manage global resources
 */
public class AgentCentreCommande extends Agent {
    // Agent state
    private String currentStatus = "READY";

    // Information collected from other agents
    private Map<String, String> fireReports = new HashMap<>(); // Map of fire location to status
    private Map<String, String> victimReports = new HashMap<>(); // Map of victim location to status
    private Map<String, String> roadReports = new HashMap<>(); // Map of road ID to status
    private Map<String, String> securityZoneReports = new HashMap<>(); // Map of zone ID to status

    // Mission planning
    private List<String> priorityMissions = new ArrayList<>(); // List of priority missions
    private Map<String, String> assignedMissions = new HashMap<>(); // Map of mission ID to assigned agent

    // Resource management
    private Map<String, Integer> availableResources = new HashMap<>(); // Map of resource type to quantity

    // Statistics
    private int totalFiresReported = 0;
    private int totalVictimsReported = 0;
    private int totalRoadIssuesReported = 0;
    private int totalMissionsDispatched = 0;

    @Override
    protected void setup() {
        System.out.println("Agent Centre Commande " + getLocalName() + " starting...");

        // Initialize resources
        initializeResources();

        // Register the agent in the yellow pages (DF)
        registerService("centre-commande");

        // Add the agent's behaviors
        addBehaviour(new CollecterInfosBehaviour(this));
        addBehaviour(new PlanifierInterventionBehaviour(this));
        addBehaviour(new DispatcherMissionsBehaviour(this));
        addBehaviour(new GererRessourcesGlobalesBehaviour(this));

        // Add a status update behavior
        addBehaviour(new TickerBehaviour(this, 10000) { // Every 10 seconds
            @Override
            protected void onTick() {
                System.out.println("Agent Centre Commande " + myAgent.getLocalName() + 
                                   " status: " + currentStatus + 
                                   ", Fires: " + totalFiresReported + 
                                   ", Victims: " + totalVictimsReported +
                                   ", Road issues: " + totalRoadIssuesReported +
                                   ", Missions: " + totalMissionsDispatched);
            }
        });

        System.out.println("Agent Centre Commande " + getLocalName() + " ready.");
    }

    /**
     * Initialize the available resources
     */
    private void initializeResources() {
        availableResources.put("fire_trucks", 10);
        availableResources.put("ambulances", 8);
        availableResources.put("police_cars", 12);
        availableResources.put("water_supply", 5000); // liters
        availableResources.put("medical_kits", 50);
        availableResources.put("barricades", 30);
    }

    /**
     * Register the agent's services in the Directory Facilitator (DF)
     * @param type The type of service to register
     */
    private void registerService(String type) {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(type);
        sd.setName(getLocalName());
        dfd.addServices(sd);

        try { 
            DFService.register(this, dfd); 
            System.out.println("Agent Centre Commande " + getLocalName() + " registered with DF");
        } catch (FIPAException e) { 
            System.err.println("Error registering Agent Centre Commande " + getLocalName() + " with DF: " + e.getMessage());
            e.printStackTrace(); 
        }
    }

    /**
     * Update the agent's status
     * @param status The new status
     */
    public void updateStatus(String status) {
        this.currentStatus = status;
    }

    /**
     * Add a fire report
     * @param location The location of the fire
     * @param status The status of the fire
     */
    public void addFireReport(String location, String status) {
        fireReports.put(location, status);
        totalFiresReported++;
    }

    /**
     * Add a victim report
     * @param location The location of the victim
     * @param status The status of the victim
     */
    public void addVictimReport(String location, String status) {
        victimReports.put(location, status);
        totalVictimsReported++;
    }

    /**
     * Add a road report
     * @param roadId The ID of the road
     * @param status The status of the road
     */
    public void addRoadReport(String roadId, String status) {
        roadReports.put(roadId, status);
        totalRoadIssuesReported++;
    }

    /**
     * Add a security zone report
     * @param zoneId The ID of the zone
     * @param status The status of the zone
     */
    public void addSecurityZoneReport(String zoneId, String status) {
        securityZoneReports.put(zoneId, status);
    }

    /**
     * Add a priority mission
     * @param mission The mission description
     */
    public void addPriorityMission(String mission) {
        priorityMissions.add(mission);
    }

    /**
     * Assign a mission to an agent
     * @param missionId The ID of the mission
     * @param agentId The ID of the agent
     */
    public void assignMission(String missionId, String agentId) {
        assignedMissions.put(missionId, agentId);
        totalMissionsDispatched++;
    }

    /**
     * Update a resource quantity
     * @param resourceType The type of resource
     * @param quantity The new quantity
     */
    public void updateResource(String resourceType, int quantity) {
        availableResources.put(resourceType, quantity);
    }

    /**
     * Get the quantity of a resource
     * @param resourceType The type of resource
     * @return The quantity of the resource
     */
    public int getResourceQuantity(String resourceType) {
        return availableResources.getOrDefault(resourceType, 0);
    }

    /**
     * Get the status of a fire
     * @param location The location of the fire
     * @return The status of the fire
     */
    public String getFireStatus(String location) {
        return fireReports.get(location);
    }

    /**
     * Get the status of a victim
     * @param location The location of the victim
     * @return The status of the victim
     */
    public String getVictimStatus(String location) {
        return victimReports.get(location);
    }

    /**
     * Get the status of a road
     * @param roadId The ID of the road
     * @return The status of the road
     */
    public String getRoadStatus(String roadId) {
        return roadReports.get(roadId);
    }

    /**
     * Get the status of a security zone
     * @param zoneId The ID of the zone
     * @return The status of the zone
     */
    public String getSecurityZoneStatus(String zoneId) {
        return securityZoneReports.get(zoneId);
    }

    /**
     * Get the list of priority missions
     * @return The list of priority missions
     */
    public List<String> getPriorityMissions() {
        return new ArrayList<>(priorityMissions);
    }

    /**
     * Get the map of assigned missions
     * @return The map of assigned missions
     */
    public Map<String, String> getAssignedMissions() {
        return new HashMap<>(assignedMissions);
    }

    /**
     * Get the total number of fires reported
     * @return The total number of fires reported
     */
    public int getTotalFiresReported() {
        return totalFiresReported;
    }

    /**
     * Get the total number of victims reported
     * @return The total number of victims reported
     */
    public int getTotalVictimsReported() {
        return totalVictimsReported;
    }

    /**
     * Get the total number of road issues reported
     * @return The total number of road issues reported
     */
    public int getTotalRoadIssuesReported() {
        return totalRoadIssuesReported;
    }

    /**
     * Get the total number of missions dispatched
     * @return The total number of missions dispatched
     */
    public int getTotalMissionsDispatched() {
        return totalMissionsDispatched;
    }

    @Override
    protected void takeDown() {
        // Deregister from the DF
        try {
            DFService.deregister(this);
            System.out.println("Agent Centre Commande " + getLocalName() + " deregistered from DF");
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        System.out.println("Agent Centre Commande " + getLocalName() + " terminating.");
    }
}
